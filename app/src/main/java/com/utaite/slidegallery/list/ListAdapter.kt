package com.utaite.slidegallery.list

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.utaite.slidegallery.R
import com.utaite.slidegallery.full.FullActivity
import com.utaite.slidegallery.util.getColorInt
import kotlinx.android.synthetic.main.activity_list_view.view.*


class ListAdapter(private val context: Context,
                  private var dataSet: MutableList<ListModel>) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private val counts: MutableList<Int> = mutableListOf()
    private var isCountAdd: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.activity_list_view, parent, false)).apply {
                itemView.listViewImage.setOnClickListener {
                    val intent = Intent(context, FullActivity::class.java).apply {
                        putExtra(FullActivity.IMAGE, dataSet[adapterPosition].image.toString())
                    }
                    context.startActivity(intent)
                }
                itemView.listViewImage.setOnLongClickListener {
                    dataSet[adapterPosition].isChoice = !dataSet[adapterPosition].isChoice

                    isCountAdd = false
                    when (dataSet[adapterPosition].isChoice) {
                        true -> {
                            val count = dataSet.filter { it.isChoice }.count()
                            var current = count
                            (1..count).forEach {
                                when {
                                    isCountAdd -> return@forEach
                                    !counts.contains(it) -> {
                                        current = it
                                        counts.add(current)
                                        isCountAdd = true
                                        return@forEach
                                    }
                                }
                            }
                            dataSet[adapterPosition].count = current

                            val activity = context as ListActivity
                            activity.supportActionBar?.title = "$count${context.getString(R.string.count_select)}"
                            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            activity.item?.isVisible = true
                        }
                        false -> {
                            counts.remove(dataSet[adapterPosition].count)
                            dataSet[adapterPosition].count = 0

                            val count = dataSet.filter { it.isChoice }.count()
                            val activity = context as ListActivity
                            when {
                                count == 0 -> {
                                    activity.supportActionBar?.title = context.getString(R.string.list_title)
                                    activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                                    activity.item?.isVisible = false
                                }
                                else -> activity.supportActionBar?.title = "$count${context.getString(R.string.count_select)}"
                            }
                        }
                    }
                    notifyDataSetChanged()
                    false
                }
            }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = dataSet[position]

        holder.itemView.listViewImage.setImageBitmap(model.thumbnail)
        when (model.isChoice) {
            false -> {
                holder.itemView.listViewLayout.setBackgroundColor(getColorInt(context, android.R.color.white))
                holder.itemView.listViewImage.clearColorFilter()
                holder.itemView.listViewCount.visibility = View.GONE
            }
            true -> {
                holder.itemView.listViewLayout.setBackgroundColor(getColorInt(context, R.color.colorAccent))
                holder.itemView.listViewImage.setColorFilter(getColorInt(context, android.R.color.darker_gray), PorterDuff.Mode.MULTIPLY)
                holder.itemView.listViewCount.visibility = View.VISIBLE
            }
        }

        holder.itemView.listViewCount.run {
            setStrokeWidth(1)
            setStrokeCol(getColorInt(context, android.R.color.darker_gray))
            setSolidCol(getColorInt(context, android.R.color.white))
            text = model.count.toString()
        }
    }

    override fun getItemCount(): Int =
            dataSet.size

    fun setData(dataSet: MutableList<ListModel>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    fun getChoiceData(): List<ListModel> =
            dataSet.filter { it.isChoice }

    fun clearData() {
        dataSet.clear()
    }

    fun clearChoice() {
        counts.clear()
        dataSet.forEach {
            it.count = 0
            it.isChoice = false
        }

        val activity = context as ListActivity
        activity.supportActionBar?.title = context.getString(R.string.list_title)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity.item?.isVisible = false
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
