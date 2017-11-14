package com.inspection.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

import com.inspection.R
import com.inspection.model.SafetyCheckReportModel

import java.util.ArrayList

/**
 * Created by sheri on 5/8/2017.
 */

class SafetyCheckShopInitialRecyclerViewAdapter(private val context: Context, private val safetyCheckListView: RecyclerView, private val toDo: ArrayList<SafetyCheckReportModel>
,private val inProgress: ArrayList<SafetyCheckReportModel>, private val completed: ArrayList<SafetyCheckReportModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeHeader = 0
    private val viewTypeNormal = 1

    var mClickListener: View.OnClickListener? = null

    fun setClickListener(callback: View.OnClickListener) {
        mClickListener = callback
    }

    inner class MyReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var reportDateTextView: TextView
        var reportStatusTextView: TextView

        init {
            reportDateTextView = view.findViewById<TextView>(R.id.safetyCheckReportDateTextView)
            reportStatusTextView = view.findViewById<TextView>(R.id.safetyCheckReportStatusTextView)
        }
    }

    inner class MyHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var reportHeaderTextView: TextView
        var safetyCheckItemCircleIndicator : View

        init {
            reportHeaderTextView = view.findViewById<TextView>(R.id.safetyCheckHeaderCategoryTextView)
            safetyCheckItemCircleIndicator = view.findViewById<View>(R.id.safetyCheckItemCircleIndicator)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View

        if (viewType == viewTypeHeader) {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.safety_check_header_layout, parent, false)
            return MyHeaderViewHolder(itemView)
        } else {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.safety_check_reports_list, parent, false)
            itemView.setOnClickListener { view -> mClickListener?.onClick(view) }
            return MyReportViewHolder(itemView)
        }

    }

    override fun getItemViewType(position: Int): Int {
        val inProgressIndex = if (toDo.size > 0) toDo.size+3 else 3
        val completedIndex = if (inProgress.size > 0) inProgressIndex + inProgress.size+1 else inProgressIndex + 1

        when (position){
            0, 2, inProgressIndex, completedIndex -> return viewTypeHeader
            else -> return viewTypeNormal
        }

//        if (safetyCheckReportModelArrayList[position].safetyCheckReportsID <= -500) {
//            return viewTypeHeader
//        } else {
//            return viewTypeNormal
//        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val inProgressIndex = if (toDo.size > 0) toDo.size + 3 else 3
        val completedIndex = if (inProgress.size > 0) inProgressIndex + inProgress.size+1 else inProgressIndex+1

        when (position){
            0, 2, inProgressIndex, completedIndex -> {
                (holder as MyHeaderViewHolder).safetyCheckItemCircleIndicator.visibility = GONE
                when (position){
                    0 ->  (holder as MyHeaderViewHolder).reportHeaderTextView.text = "NEW"
                    2 -> (holder as MyHeaderViewHolder).reportHeaderTextView.text = "TODO"
                    inProgressIndex -> (holder as MyHeaderViewHolder).reportHeaderTextView.text = "InProgress"
                    completedIndex -> (holder as MyHeaderViewHolder).reportHeaderTextView.text = "DONE"
                }
            }
            else -> {
                when (position){

                    1 -> {
                        (holder as MyReportViewHolder).reportDateTextView.text = "Start New Safety Check Report"
                        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                        (holder as MyReportViewHolder).reportDateTextView.layoutParams= layoutParams
                        (holder as MyReportViewHolder).reportStatusTextView.text = ""
                    }

//                    else ->{
//                        (holder as MyReportViewHolder).reportDateTextView.text = ""
//                        (holder as MyReportViewHolder).reportDateTextView.text = ""
//                    }
                    in 3..inProgressIndex -> {
                        (holder as MyReportViewHolder).reportDateTextView.text = toDo[position-3].safetyCheckReportDate
                        holder.reportStatusTextView.text = toDo[position-3].customerName
                    }

                    in inProgressIndex..completedIndex -> {
                        (holder as MyReportViewHolder).reportDateTextView.text = inProgress[position-inProgressIndex-1].safetyCheckReportDate
                        (holder as MyReportViewHolder).reportStatusTextView.text = inProgress[position-inProgressIndex-1].customerName
                    }

                     else ->{
                        (holder as MyReportViewHolder).reportDateTextView.text = completed[position-completedIndex-1].safetyCheckReportDate
                        (holder as MyReportViewHolder).reportStatusTextView.text = completed[position-completedIndex-1].customerName
                    }
                }


            }
        }
    }

    fun getSafetyCheckReportItem(position: Int): SafetyCheckReportModel{
        val inProgressIndex = if (toDo.size > 0) toDo.size + 3 else 3
        val completedIndex = if (inProgress.size > 0) inProgressIndex + inProgress.size+1 else inProgressIndex+1

        when (position){
            in 3..inProgressIndex -> {
                return toDo[position-3]
            }

            in inProgressIndex..completedIndex -> {
                return inProgress[position-inProgressIndex-1]
            }

            else -> {
                return completed[position-completedIndex-1]
            }
        }
    }

    override fun getItemCount(): Int {
        return toDo.size + inProgress.size + completed.size + 5
    }

}