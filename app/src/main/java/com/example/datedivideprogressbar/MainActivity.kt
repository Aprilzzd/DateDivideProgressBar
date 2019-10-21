package com.example.datedivideprogressbar

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val progressBeanList= mutableListOf<DivideProgressBar.ProgressBean>()
        progressBeanList.apply {
            add(DivideProgressBar.ProgressBean("购买日", "2019-10-15"))
            add(DivideProgressBar.ProgressBean("起息日", "2019-10-21"))
            add(DivideProgressBar.ProgressBean("到期日,资金可用", "2019-10-27"))
            add(DivideProgressBar.ProgressBean("资金可取日", "2019-11-10"))
        }
        progressbar.setList(progressBeanList)

        button.text="2019-10-15"
        button.setOnClickListener {
            progressbar.setDate("2019-10-15")
        }

        button2.text="2019-10-26"
        button2.setOnClickListener {
            progressbar.setDate("2019-10-26")
        }

        button3.text="2019-11-10"
        button3.setOnClickListener {
            progressbar.setDate("2019-11-10")
        }
    }
}
