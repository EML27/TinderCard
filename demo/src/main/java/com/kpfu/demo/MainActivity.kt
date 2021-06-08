package com.kpfu.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import com.kpfu.tindercard.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val manager = StackLayoutManager(this).apply {
        onCardSwipeListener = { paginate() }
    }
    private val adapter = StackAdapter(createList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupStackView()
        setupButton()
    }

    private fun setupStackView() {
        manager.setStackFrom(StackForm.TOP)
        manager.setVisibleCount(3)
        manager.setScaleInterval(0.98f)
        manager.setDirections(Direction.HORIZONTAL)
        stackView.layoutManager = manager
        stackView.adapter = adapter
    }

    private fun setupButton() {
        btnDeny.setOnClickListener {
            val setting = SwipeAnimationSettings(
                direction = Direction.LEFT,
                duration = Duration.NORMAL.duration,
            )
            manager.setSwipeAnimationSetting(setting)
            stackView.swipe()
        }
        btnCancel.setOnClickListener {
            val setting = RewindAnimationSettings(
                direction = Direction.BOTTOM
            )
            manager.setRewindAnimationSetting(setting)
            stackView.rewind()
        }
        btnAccept.setOnClickListener {
            val setting = SwipeAnimationSettings(
                direction = Direction.RIGHT,
                duration = Duration.NORMAL.duration,
            )
            manager.setSwipeAnimationSetting(setting)
            stackView.swipe()
        }
    }

    private fun paginate() {
        val old = adapter.items
        val new = old.plus(Food())
        val callback = FoodDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.items = new
        result.dispatchUpdatesTo(adapter)
    }

    private fun createList(): List<Food> {
        val res = mutableListOf<Food>()
        res.add(Food())
        res.add(Food())
        res.add(Food())
        res.add(Food())
        res.add(Food())
        res.add(Food())
        res.add(Food())
        res.add(Food())
        res.add(Food())
        return res
    }
}
