package com.inspection.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

import com.inspection.inspection.R
import com.inspection.model.SafetyCheckReportModel

import java.util.ArrayList

/**
 * Created by sheri on 5/8/2017.
 */

class SafetyCheckReportsRecyclerViewAdapter(private val context: Context, private val safetyCheckReportModelArrayList: ArrayList<SafetyCheckReportModel>, private val selectedSafetyCheckReportsID: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeNormal = 0
    private val viewTypeHeader = 1


    var mClickListener: View.OnClickListener? = null

    fun setClickListener(callback: View.OnClickListener) {
        mClickListener = callback
    }

    inner class MyReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var reportDateTextView: TextView
        var reportStatusTextView: TextView
        var safetyCheckPickUpImageView: ImageView
        var safetyCheckFinalTestImageView: ImageView
        var safetyCheckWorkingImageView: ImageView
        var safetyCheckWaitingOnApprovalImageView: ImageView
        var safetyCheckWaitingOnEstimateImageView: ImageView
        var safetyCheckInspectImageView: ImageView
        var safetyCheckDropOffImageView: ImageView
        var safetyCheckWaitingOnPartsImageView: ImageView
        var dropOffCompletedTextView: TextView
        var inspectionCompletedTextView: TextView
        var waitingOnEstimateCompletedTextView: TextView
        var waitingOnApprovalCompletedTextView: TextView
        var waitingOnPartsCompletedTextView: TextView
        var workingCompletedTextView: TextView
        var finalTestCompletedTextView: TextView
        var pickupCompletedTextView: TextView
        var dropOffTextView: TextView
        var inspectionTextView: TextView
        var waitingOnEstimateTextView: TextView
        var waitingOnApprovalTextView: TextView
        var waitingOnPartsTextView: TextView
        var workingTextView: TextView
        var finalTestTextView: TextView
        var pickupTextView: TextView
        var dropOffProgressBar: ProgressBar
        var inspectProgressBar: ProgressBar
        var waitingOnEstimateProgressBar: ProgressBar
        var waitingOnApprovalProgressBar: ProgressBar
        var waitingOnPartsProgressBar: ProgressBar
        var workingProgressBar: ProgressBar
        var finalTestProgressBar: ProgressBar
        var pickupProgressBar: ProgressBar

        var dropOffLayout: RelativeLayout? = null
        var inspectLayout: RelativeLayout? = null
        var waitingOnEstimateLayout: RelativeLayout? = null
        var waitingOnApprovalLayout: RelativeLayout? = null
        var waitingOnPartsLayout: RelativeLayout? = null
        var workingLayout: RelativeLayout? = null
        var finalTestLayout: RelativeLayout? = null
        var pickupLayout: RelativeLayout? = null

        init {
            reportDateTextView = view.findViewById<TextView>(R.id.safetyCheckReportDateTextView)
            reportStatusTextView = view.findViewById<TextView>(R.id.safetyCheckReportStatusTextView)
            safetyCheckDropOffImageView = view.findViewById<ImageView>(R.id.safetyCheckDropOffImageView)
            safetyCheckInspectImageView = view.findViewById<ImageView>(R.id.safetyCheckInspectImageView)
            safetyCheckWaitingOnEstimateImageView = view.findViewById<ImageView>(R.id.safetyCheckWaitingOnEstimateImageView)
            safetyCheckWaitingOnApprovalImageView = view.findViewById<ImageView>(R.id.safetyCheckWaitingOnApprovalImageView)
            safetyCheckWaitingOnPartsImageView = view.findViewById<ImageView>(R.id.safetyCheckWaitingOnPartsImageView)
            safetyCheckWorkingImageView = view.findViewById<ImageView>(R.id.safetyCheckWorkingImageView)
            safetyCheckFinalTestImageView = view.findViewById<ImageView>(R.id.safetyCheckFinalTestImageView)
            safetyCheckPickUpImageView = view.findViewById<ImageView>(R.id.safetyCheckPickUpImageView)

            dropOffTextView = view.findViewById<TextView>(R.id.safetyCheckDropOffTextView)
            inspectionTextView = view.findViewById<TextView>(R.id.safetyCheckInspectTextView)
            waitingOnEstimateTextView = view.findViewById<TextView>(R.id.safetyCheckWaitingOnEstimateTextView)
            waitingOnApprovalTextView = view.findViewById<TextView>(R.id.safetyCheckWaitingOnApprovalTextView)
            waitingOnPartsTextView = view.findViewById<TextView>(R.id.safetyCheckWaitingOnPartsTextView)
            workingTextView = view.findViewById<TextView>(R.id.safetyCheckWorkingTextView)
            finalTestTextView = view.findViewById<TextView>(R.id.safetyCheckFinalTestTextView)
            pickupTextView = view.findViewById<TextView>(R.id.safetyCheckPickUpTextView)

            dropOffCompletedTextView = view.findViewById<TextView>(R.id.safetyCheckDropOffCompletedTextView)
            inspectionCompletedTextView = view.findViewById<TextView>(R.id.safetyCheckInspectCompletedTextView)
            waitingOnEstimateCompletedTextView = view.findViewById<TextView>(R.id.safetyCheckWaitingOnEstimateCompletedTextView)
            waitingOnApprovalCompletedTextView = view.findViewById<TextView>(R.id.safetyCheckWaitingOnApprovalCompletedTextView)
            waitingOnPartsCompletedTextView = view.findViewById<TextView>(R.id.safetyCheckWaitingOnPartsCompletedTextView)
            workingCompletedTextView = view.findViewById<TextView>(R.id.safetyCheckWorkingCompletedTextView)
            finalTestCompletedTextView = view.findViewById<TextView>(R.id.safetyCheckFinalTestCompletedTextView)
            pickupCompletedTextView = view.findViewById<TextView>(R.id.safetyCheckPickUpCompletedTextView)

            dropOffLayout = view.findViewById<RelativeLayout>(R.id.dropOffLayout)
            inspectLayout = view.findViewById<RelativeLayout>(R.id.inspectLayout)
            waitingOnEstimateLayout = view.findViewById<RelativeLayout>(R.id.waitingOnEstimateLayout)
            waitingOnApprovalLayout = view.findViewById<RelativeLayout>(R.id.waitingOnApprovalLayout)
            waitingOnPartsLayout = view.findViewById<RelativeLayout>(R.id.waitingOnPartsLayout)
            workingLayout = view.findViewById<RelativeLayout>(R.id.workingLayout)
            finalTestLayout = view.findViewById<RelativeLayout>(R.id.finalTestLayout)
            pickupLayout = view.findViewById<RelativeLayout>(R.id.pickupLayout)

            dropOffProgressBar = view.findViewById<ProgressBar>(R.id.dropOffProgressBar)
            dropOffProgressBar.indeterminateDrawable.setColorFilter(context.getColor(R.color.green), PorterDuff.Mode.MULTIPLY)

            inspectProgressBar = view.findViewById<ProgressBar>(R.id.inspectProgressBar)
            inspectProgressBar.indeterminateDrawable.setColorFilter(context.getColor(R.color.green), PorterDuff.Mode.MULTIPLY)

            waitingOnEstimateProgressBar = view.findViewById<ProgressBar>(R.id.waitingOnEstimateProgressBar)
            waitingOnEstimateProgressBar.indeterminateDrawable.setColorFilter(context.getColor(R.color.green), PorterDuff.Mode.MULTIPLY)

            waitingOnApprovalProgressBar = view.findViewById<ProgressBar>(R.id.waitingOnApprovalProgressBar)
            waitingOnApprovalProgressBar.indeterminateDrawable.setColorFilter(context.getColor(R.color.green), PorterDuff.Mode.MULTIPLY)

            waitingOnPartsProgressBar = view.findViewById<ProgressBar>(R.id.waitingOnPartsProgressBar)
            waitingOnPartsProgressBar.indeterminateDrawable.setColorFilter(context.getColor(R.color.green), PorterDuff.Mode.MULTIPLY)

            workingProgressBar = view.findViewById<ProgressBar>(R.id.workingProgressBar)
            workingProgressBar.indeterminateDrawable.setColorFilter(context.getColor(R.color.green), PorterDuff.Mode.MULTIPLY)

            finalTestProgressBar = view.findViewById<ProgressBar>(R.id.finalTestProgressBar)
            finalTestProgressBar.indeterminateDrawable.setColorFilter(context.getColor(R.color.green), PorterDuff.Mode.MULTIPLY)

            pickupProgressBar = view.findViewById<ProgressBar>(R.id.pickUpProgressBar)
            pickupProgressBar.indeterminateDrawable.setColorFilter(context.getColor(R.color.green), PorterDuff.Mode.MULTIPLY)

        }
    }

    inner class MyHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var reportHeaderTextView: TextView
        var safetyCheckItemCircleIndicator: View

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
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.safety_check_reports_list_uncompleted, parent, false)
            itemView.setOnClickListener { view -> mClickListener!!.onClick(view) }
            return MyReportViewHolder(itemView)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (safetyCheckReportModelArrayList[position].safetyCheckReportsID == -500) {
            return viewTypeHeader
        } else {
            return viewTypeNormal
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val reportItem = safetyCheckReportModelArrayList[position]


        if (reportItem.safetyCheckReportsID == -500) {
            (holder as MyHeaderViewHolder).reportHeaderTextView.text = safetyCheckReportModelArrayList[position].MMY
            (holder as MyHeaderViewHolder).safetyCheckItemCircleIndicator.visibility = GONE
        } else {
            (holder as MyReportViewHolder).reportDateTextView.text = safetyCheckReportModelArrayList[position].safetyCheckReportDate
            if (safetyCheckReportModelArrayList[position].status == 3) {
                holder.reportStatusTextView.setTextColor(context.getColor(R.color.green))
                holder.reportStatusTextView.text = "Completed"
                holder.dropOffLayout!!.visibility = View.GONE
                holder.inspectLayout!!.visibility = View.GONE
                holder.waitingOnEstimateLayout!!.visibility = View.GONE
                holder.waitingOnApprovalLayout!!.visibility = View.GONE
                holder.waitingOnPartsLayout!!.visibility = View.GONE
                holder.workingLayout!!.visibility = View.GONE
                holder.finalTestLayout!!.visibility = View.GONE
                holder.pickupLayout!!.visibility = View.GONE
                return
            } else {
                holder.reportStatusTextView.setTextColor(Color.RED)
                holder.reportStatusTextView.text = "Not Completed"
            }

            when (safetyCheckReportModelArrayList[position].fullProgressId) {
                8 -> {
                    holder.safetyCheckPickUpImageView.visibility = View.VISIBLE
                    holder.pickupCompletedTextView.text = " Completed"
                    holder.safetyCheckFinalTestImageView.visibility = View.VISIBLE
                    holder.finalTestCompletedTextView.text = " Completed"
                    holder.safetyCheckWorkingImageView.visibility = View.VISIBLE
                    holder.workingCompletedTextView.text = " Completed"
                    holder.safetyCheckWaitingOnPartsImageView.visibility = View.VISIBLE
                    holder.waitingOnPartsCompletedTextView.text = " Completed"
                    holder.safetyCheckWaitingOnApprovalImageView.visibility = View.VISIBLE
                    holder.waitingOnApprovalCompletedTextView.text = " Completed"
                    holder.safetyCheckWaitingOnEstimateImageView.visibility = View.VISIBLE
                    holder.waitingOnEstimateCompletedTextView.text = " Completed"
                    holder.safetyCheckInspectImageView.visibility = View.VISIBLE
                    holder.inspectionCompletedTextView.text = " Completed"
                    holder.safetyCheckDropOffImageView.visibility = View.VISIBLE
                    holder.dropOffCompletedTextView.text = " Completed"
                }
                7 -> {
                    holder.safetyCheckFinalTestImageView.visibility = View.VISIBLE
                    holder.finalTestCompletedTextView.text = " Completed"
                    holder.safetyCheckWorkingImageView.visibility = View.VISIBLE
                    holder.workingCompletedTextView.text = " Completed"
                    holder.safetyCheckWaitingOnPartsImageView.visibility = View.VISIBLE
                    holder.waitingOnPartsCompletedTextView.text = " Completed"
                    holder.safetyCheckWaitingOnApprovalImageView.visibility = View.VISIBLE
                    holder.waitingOnApprovalCompletedTextView.text = " Completed"
                    holder.safetyCheckWaitingOnEstimateImageView.visibility = View.VISIBLE
                    holder.waitingOnEstimateCompletedTextView.text = " Completed"
                    holder.safetyCheckInspectImageView.visibility = View.VISIBLE
                    holder.inspectionCompletedTextView.text = " Completed"
                    holder.safetyCheckDropOffImageView.visibility = View.VISIBLE
                    holder.dropOffCompletedTextView.text = " Completed"
                }
                6 -> {
                    holder.safetyCheckWorkingImageView.visibility = View.VISIBLE
                    holder.workingCompletedTextView.text = " Completed"
                    holder.safetyCheckWaitingOnPartsImageView.visibility = View.VISIBLE
                    holder.waitingOnPartsCompletedTextView.text = " Completed"
                    holder.safetyCheckWaitingOnApprovalImageView.visibility = View.VISIBLE
                    holder.waitingOnApprovalCompletedTextView.text = " Completed"
                    holder.safetyCheckWaitingOnEstimateImageView.visibility = View.VISIBLE
                    holder.waitingOnEstimateCompletedTextView.text = " Completed"
                    holder.safetyCheckInspectImageView.visibility = View.VISIBLE
                    holder.inspectionCompletedTextView.text = " Completed"
                    holder.safetyCheckDropOffImageView.visibility = View.VISIBLE
                    holder.dropOffCompletedTextView.text = " Completed"
                }
                5 -> {
                    holder.safetyCheckWaitingOnPartsImageView.visibility = View.VISIBLE
                    holder.waitingOnPartsCompletedTextView.text = " Completed"
                    holder.safetyCheckWaitingOnApprovalImageView.visibility = View.VISIBLE
                    holder.waitingOnApprovalCompletedTextView.text = " Completed"
                    holder.safetyCheckWaitingOnEstimateImageView.visibility = View.VISIBLE
                    holder.waitingOnEstimateCompletedTextView.text = " Completed"
                    holder.safetyCheckInspectImageView.visibility = View.VISIBLE
                    holder.inspectionCompletedTextView.text = " Completed"
                    holder.safetyCheckDropOffImageView.visibility = View.VISIBLE
                    holder.dropOffCompletedTextView.text = " Completed"
                }
                4 -> {
                    holder.safetyCheckWaitingOnApprovalImageView.visibility = View.VISIBLE
                    holder.waitingOnApprovalCompletedTextView.text = " Completed"
                    holder.safetyCheckWaitingOnEstimateImageView.visibility = View.VISIBLE
                    holder.waitingOnEstimateCompletedTextView.text = " Completed"
                    holder.safetyCheckInspectImageView.visibility = View.VISIBLE
                    holder.inspectionCompletedTextView.text = " Completed"
                    holder.safetyCheckDropOffImageView.visibility = View.VISIBLE
                    holder.dropOffCompletedTextView.text = " Completed"
                }
                3 -> {
                    holder.safetyCheckWaitingOnEstimateImageView.visibility = View.VISIBLE
                    holder.waitingOnEstimateCompletedTextView.text = " Completed"
                    holder.safetyCheckInspectImageView.visibility = View.VISIBLE
                    holder.inspectionCompletedTextView.text = " Completed"
                    holder.safetyCheckDropOffImageView.visibility = View.VISIBLE
                    holder.dropOffCompletedTextView.text = " Completed"
                }
                2 -> {
                    holder.safetyCheckInspectImageView.visibility = View.VISIBLE
                    holder.inspectionCompletedTextView.text = " Completed"
                    holder.safetyCheckDropOffImageView.visibility = View.VISIBLE
                    holder.dropOffCompletedTextView.text = " Completed"
                }
                1 -> {
                    holder.safetyCheckDropOffImageView.visibility = View.VISIBLE
                    holder.dropOffCompletedTextView.text = " Completed"
                }
            }

            when (safetyCheckReportModelArrayList[position].fullProgressId) {
                7 -> {
                    holder.safetyCheckPickUpImageView.visibility = View.INVISIBLE
                    holder.pickupProgressBar.visibility = View.VISIBLE
                    holder.pickupCompletedTextView.text = " InProgress"
                    holder.safetyCheckFinalTestImageView.visibility = View.INVISIBLE
                    holder.finalTestProgressBar.visibility = View.VISIBLE
                    holder.finalTestCompletedTextView.text = " InProgress"
                    holder.safetyCheckWorkingImageView.visibility = View.INVISIBLE
                    holder.workingProgressBar.visibility = View.VISIBLE
                    holder.workingCompletedTextView.text = " InProgress"
                    holder.safetyCheckWaitingOnPartsImageView.visibility = View.INVISIBLE
                    holder.waitingOnPartsProgressBar.visibility = View.VISIBLE
                    holder.waitingOnPartsCompletedTextView.text = " InProgress"
                    holder.safetyCheckWaitingOnApprovalImageView.visibility = View.INVISIBLE
                    holder.waitingOnApprovalProgressBar.visibility = View.VISIBLE
                    holder.waitingOnApprovalCompletedTextView.text = " InProgress"
                }
                6 -> {
                    holder.safetyCheckFinalTestImageView.visibility = View.INVISIBLE
                    holder.finalTestProgressBar.visibility = View.VISIBLE
                    holder.finalTestCompletedTextView.text = " InProgress"
                    holder.safetyCheckWorkingImageView.visibility = View.INVISIBLE
                    holder.workingProgressBar.visibility = View.VISIBLE
                    holder.workingCompletedTextView.text = " InProgress"
                    holder.safetyCheckWaitingOnPartsImageView.visibility = View.INVISIBLE
                    holder.waitingOnPartsProgressBar.visibility = View.VISIBLE
                    holder.waitingOnPartsCompletedTextView.text = " InProgress"
                    holder.safetyCheckWaitingOnApprovalImageView.visibility = View.INVISIBLE
                    holder.waitingOnApprovalProgressBar.visibility = View.VISIBLE
                    holder.waitingOnApprovalCompletedTextView.text = " InProgress"
                }
                5 -> {
                    holder.safetyCheckWorkingImageView.visibility = View.INVISIBLE
                    holder.workingProgressBar.visibility = View.VISIBLE
                    holder.workingCompletedTextView.text = " InProgress"
                    holder.safetyCheckWaitingOnPartsImageView.visibility = View.INVISIBLE
                    holder.waitingOnPartsProgressBar.visibility = View.VISIBLE
                    holder.waitingOnPartsCompletedTextView.text = " InProgress"
                    holder.safetyCheckWaitingOnApprovalImageView.visibility = View.INVISIBLE
                    holder.waitingOnApprovalProgressBar.visibility = View.VISIBLE
                    holder.waitingOnApprovalCompletedTextView.text = " InProgress"
                }
                4 -> {
                    holder.safetyCheckWaitingOnPartsImageView.visibility = View.INVISIBLE
                    holder.waitingOnPartsProgressBar.visibility = View.VISIBLE
                    holder.waitingOnPartsCompletedTextView.text = " InProgress"
                    holder.safetyCheckWaitingOnApprovalImageView.visibility = View.INVISIBLE
                    holder.waitingOnApprovalProgressBar.visibility = View.VISIBLE
                    holder.waitingOnApprovalCompletedTextView.text = " InProgress"
                }
                3 -> {
                    holder.safetyCheckWaitingOnApprovalImageView.visibility = View.INVISIBLE
                    holder.waitingOnApprovalProgressBar.visibility = View.VISIBLE
                    holder.waitingOnApprovalCompletedTextView.text = " InProgress"
                }
                2 -> {
                    holder.safetyCheckWaitingOnEstimateImageView.visibility = View.INVISIBLE
                    holder.waitingOnEstimateProgressBar.visibility = View.VISIBLE
                    holder.waitingOnEstimateCompletedTextView.text = " InProgress"
                }
                1 -> {
                    holder.safetyCheckInspectImageView.visibility = View.INVISIBLE
                    holder.inspectProgressBar.visibility = View.VISIBLE
                    holder.inspectionCompletedTextView.text = " InProgress"
                }
            }

            when (safetyCheckReportModelArrayList[position].fullProgressId) {
                7 -> {
                    //                    holder.inspectionTextView.setTextColor(Color.GRAY)
                }
                6 -> {
                    holder.pickupTextView.setTextColor(Color.GRAY)
                    holder.pickupTextView.alpha = 0.4f
                }
                5 -> {
                    holder.pickupTextView.setTextColor(Color.GRAY)
                    holder.pickupTextView.alpha = 0.4f
                    holder.finalTestTextView.setTextColor(Color.GRAY)
                    holder.finalTestTextView.alpha = 0.4f
                }
                4 -> {
                    holder.pickupTextView.setTextColor(Color.GRAY)
                    holder.pickupTextView.alpha = 0.4f
                    holder.finalTestTextView.setTextColor(Color.GRAY)
                    holder.finalTestTextView.alpha = 0.4f
                    holder.workingTextView.setTextColor(Color.GRAY)
                    holder.workingTextView.alpha = 0.4f
                }
                3 -> {
                    holder.pickupTextView.setTextColor(Color.GRAY)
                    holder.pickupTextView.alpha = 0.4f
                    holder.finalTestTextView.setTextColor(Color.GRAY)
                    holder.finalTestTextView.alpha = 0.4f
                    holder.workingTextView.setTextColor(Color.GRAY)
                    holder.workingTextView.alpha = 0.4f
                    holder.waitingOnPartsTextView.setTextColor(Color.GRAY)
                    holder.waitingOnPartsTextView.alpha = 0.4f
                }
                2 -> {
                    holder.pickupTextView.setTextColor(Color.GRAY)
                    holder.pickupTextView.alpha = 0.4f
                    holder.finalTestTextView.setTextColor(Color.GRAY)
                    holder.finalTestTextView.alpha = 0.4f
                    holder.workingTextView.setTextColor(Color.GRAY)
                    holder.workingTextView.alpha = 0.4f
                    holder.waitingOnPartsTextView.setTextColor(Color.GRAY)
                    holder.waitingOnPartsTextView.alpha = 0.4f
                    holder.waitingOnApprovalTextView.setTextColor(Color.GRAY)
                    holder.waitingOnApprovalTextView.alpha = 0.4f
                }
                1 -> {

                    holder.pickupTextView.setTextColor(Color.GRAY)
                    holder.pickupTextView.alpha = 0.4f
                    holder.finalTestTextView.setTextColor(Color.GRAY)
                    holder.finalTestTextView.alpha = 0.4f
                    holder.workingTextView.setTextColor(Color.GRAY)
                    holder.workingTextView.alpha = 0.4f
                    holder.waitingOnPartsTextView.setTextColor(Color.GRAY)
                    holder.waitingOnPartsTextView.alpha = 0.4f
                    holder.waitingOnApprovalTextView.setTextColor(Color.GRAY)
                    holder.waitingOnApprovalTextView.alpha = 0.4f
                    holder.waitingOnEstimateTextView.setTextColor(Color.GRAY)
                    holder.waitingOnEstimateTextView.alpha = 0.4f
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return safetyCheckReportModelArrayList.size
    }

}