package com.utaite.slidegallery.list

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.utaite.slidegallery.BuildConfig
import com.utaite.slidegallery.R
import com.utaite.slidegallery.slide.SlideActivity
import com.utaite.slidegallery.util.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_wait.*
import permissions.dispatcher.*


@RuntimePermissions
class ListActivity : AppCompatActivity() {

    companion object {

        val FONTS = "fonts/NanumGothic.ttf"

    }

    var item: MenuItem? = null

    private val REQ_CODE_PERMISSION = 1000

    private val listAdapter = ListAdapter(this, mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView(this, R.layout.activity_empty)

        supportActionBar?.title = getString(R.string.list_title)
        ListActivityPermissionsDispatcher.initWithPermissionCheck(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        item = menu.getItem(0)
        item?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.listMenuCheck -> {
                    val data = listAdapter.getChoiceData().map { it.image.toString() }.toTypedArray()
                    val intent = Intent(this, SlideActivity::class.java).apply {
                        putExtra(SlideActivity.IMAGE, data)
                    }
                    startActivity(intent)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ListActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_PERMISSION -> {
                val check = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                when (check) {
                    PackageManager.PERMISSION_DENIED -> onPermissionDenied()
                    PackageManager.PERMISSION_GRANTED -> init()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        listAdapter.clearChoice()
        return super.onSupportNavigateUp()
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun init() {
        setView(this, R.layout.activity_wait)
        waitText.typeface = Typeface.createFromAsset(assets, ListActivity.FONTS)

        Observable.just(mutableListOf<ListModel>())
                .map { getDataSet(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { dataSet ->
                    setView(this, R.layout.activity_list)
                    listAdapter.setData(dataSet)
                    listRecyclerView.run {
                        setHasFixedSize(true)
                        layoutManager = StaggeredGridLayoutManager(2, 1).apply {
                            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
                            orientation = StaggeredGridLayoutManager.VERTICAL
                        }
                        adapter = listAdapter
                    }
                    listSwipeLayout.run {
                        setOnRefreshListener {
                            listAdapter.run {
                                clearChoice()
                                clearData()
                                notifyDataSetChanged()
                            }
                            Observable.just(mutableListOf<ListModel>())
                                    .map { getDataSet(it) }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe { dataSet ->
                                        listAdapter.setData(dataSet)
                                        listSwipeLayout.isRefreshing = false
                                    }
                        }
                        setColorSchemeResources(R.color.listRefreshColor1, R.color.listRefreshColor2)
                        isRefreshing = false
                    }
                }
    }

    private fun getDataSet(dataSet: MutableList<ListModel>): MutableList<ListModel> {
        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory().absolutePath)))

        val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null, null, null)

        val imageIndex = cursor.getColumnIndex(projection[0])
        val thumbnailIndex = cursor.getColumnIndex(projection[1])

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val image = Uri.parse(cursor.getString(imageIndex))
                    val thumbnail = MediaStore.Images.Thumbnails.getThumbnail(contentResolver,
                            cursor.getString(thumbnailIndex).toLong(), MediaStore.Images.Thumbnails.MINI_KIND, null)

                    val model = ListModel(image, thumbnail)
                    dataSet.add(model)
                } while (it.moveToNext())
            }
        }
        return dataSet
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onShowRationale(request: PermissionRequest) {
        getDialog(this, R.string.permission_rationale, { request.proceed() }, { request.cancel() })
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onPermissionDenied() {
        getToast(this, R.string.permission_denied)
        finish()
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onNeverAskAgain() {
        getDialog(this, R.string.permission_never_ask_again, {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID))
            startActivityForResult(intent, REQ_CODE_PERMISSION)
        }, { onPermissionDenied() })
    }

}
