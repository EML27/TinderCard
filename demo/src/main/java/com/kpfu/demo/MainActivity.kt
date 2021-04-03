package com.kpfu.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import com.kpfu.tindercard.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), StackListener {

    private val manager = StackLayoutManager(this, this)
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

    override fun onCardDrag(direction: Direction, ratio: Float) {
        //TODO("Not yet implemented")
    }

    override fun onCardSwipe(direction: Direction) {
        paginate()
    }

    override fun onCardRewound() {
        //TODO("Not yet implemented")
    }

    override fun onCardCancel() {
        //TODO("Not yet implemented")
    }

    override fun onCardAppear(view: View, position: Int) {
        //TODO("Not yet implemented")
    }

    override fun onCardDisappear(view: View, position: Int) {
        //TODO("Not yet implemented")
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
