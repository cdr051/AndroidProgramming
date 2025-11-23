package com.example.cospicker.stay.search

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.example.cospicker.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar

class StayDatePeopleBottomSheet(
    private val onApply: (String, String, Int) -> Unit
) : BottomSheetDialogFragment() {

    // 날짜를 직접 year/month/day 로 저장 (java.time 안 씀)
    private var startY: Int? = null
    private var startM: Int? = null
    private var startD: Int? = null

    private var endY: Int? = null
    private var endM: Int? = null
    private var endD: Int? = null

    private var people = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_date_people, null)
        dialog.setContentView(view)

        val txtSelectedDate = view.findViewById<TextView>(R.id.txtSelectedDate)
        val txtPeopleTitle = view.findViewById<TextView>(R.id.txtPeopleTitle)
        val txtPeopleCount = view.findViewById<TextView>(R.id.txtPeopleCount)

        // 🔸 날짜 선택 버튼 클릭 → DatePickerDialog 두 번 사용 (시작/끝)
        view.findViewById<TextView>(R.id.btnSelectDate).setOnClickListener {
            val cal = Calendar.getInstance()
            val picker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    if (startY == null || endY != null) {
                        // 시작 날짜 설정
                        startY = year
                        startM = month + 1
                        startD = dayOfMonth

                        endY = null
                        endM = null
                        endD = null

                        txtSelectedDate.text = "${startM}.${startD} 선택"
                    } else {
                        // 끝 날짜 설정
                        endY = year
                        endM = month + 1
                        endD = dayOfMonth

                        txtSelectedDate.text =
                            "${startM}.${startD} ~ ${endM}.${endD}"
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            picker.show()
        }

        // 🔸 인원 +
        view.findViewById<TextView>(R.id.btnPlus).setOnClickListener {
            people++
            txtPeopleCount.text = people.toString()
            txtPeopleTitle.text = "인원 $people"
        }

        // 🔸 인원 -
        view.findViewById<TextView>(R.id.btnMinus).setOnClickListener {
            if (people > 1) {
                people--
                txtPeopleCount.text = people.toString()
                txtPeopleTitle.text = "인원 $people"
            }
        }

        // 🔸 적용 버튼
        view.findViewById<Button>(R.id.btnApply).setOnClickListener {
            val startStr = if (startY != null) {
                "${startM}.${startD}"
            } else {
                ""
            }

            val endStr = if (endY != null) {
                "${endM}.${endD}"
            } else {
                ""
            }

            onApply(startStr, endStr, people)
            dismiss()
        }

        return dialog
    }
}
