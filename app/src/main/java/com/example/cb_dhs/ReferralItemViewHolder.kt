package com.example.cb_dhs

import android.content.res.Resources
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cb_dhs.databinding.ReferralListItemViewBinding
import java.time.LocalDate
import java.time.Period

class ReferralItemViewHolder(binding: ReferralListItemViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val statusView: LinearLayout = binding.status
    private val nameView: TextView = binding.name
    private val ageView: TextView = binding.fieldName
    private val idView: TextView = binding.id

    fun bindTo(
        referralItem: ReferralListViewModel.ReferralItem,
        onItemClicked: (ReferralListViewModel.ReferralItem) -> Unit,
    ) {
        this.nameView.text = referralItem.name
        this.ageView.text = getFormattedAge(referralItem, ageView.context.resources)
        this.idView.text = "Id: #---${getTruncatedId(referralItem)}"
        this.itemView.setOnClickListener { onItemClicked(referralItem) }
//        statusView.imageTintList =
//            ColorStateList.valueOf(
//                ContextCompat.getColor(
//                    statusView.context,
//                    when (patientItem.risk) {
//                        RiskProbability.HIGH.toCode() -> R.color.high_risk
//                        RiskProbability.MODERATE.toCode() -> R.color.moderate_risk
//                        RiskProbability.LOW.toCode() -> R.color.low_risk
//                        else -> R.color.unknown_risk
//                    },
//                ),
//            )
    }

    private fun getFormattedAge(
        referralItem: ReferralListViewModel.ReferralItem,
        resources: Resources,
    ): String {
        if (referralItem.dob == null) return ""
        return Period.between(referralItem.dob, LocalDate.now()).let {
            when {
                it.years > 0 -> resources.getQuantityString(R.plurals.ageYear, it.years, it.years)
                it.months > 0 -> resources.getQuantityString(
                    R.plurals.ageMonth,
                    it.months,
                    it.months
                )

                else -> resources.getQuantityString(R.plurals.ageDay, it.days, it.days)
            }
        }
    }

    /** The new ui just shows shortened id with just last 3 characters. */
    private fun getTruncatedId(referralItem: ReferralListViewModel.ReferralItem): String {
        return referralItem.resourceId.takeLast(3)
    }
}
