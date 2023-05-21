package com.project.ummsungpay

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_cardlist.card_view

class CardlistFragment : Fragment() {

    private lateinit var cardlistAdapter: cardlist_adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cardlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var list: ArrayList<CardData> = requireActivity().intent!!.extras!!.get("CardList") as ArrayList<CardData>
        Log.e("CardlistFragment", "Card List: ${list}")

        cardlistAdapter = cardlist_adapter(list)
        card_view.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        card_view.adapter = cardlistAdapter

        //아이템 간 간격 추가
        val spaceDecoration = VerticalSpaceItemDecoration(18)
        card_view.addItemDecoration(spaceDecoration)
    }

    inner class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.bottom = verticalSpaceHeight
        }
    }
}