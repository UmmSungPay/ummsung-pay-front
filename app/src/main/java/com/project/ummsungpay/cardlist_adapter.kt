package com.project.ummsungpay

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class cardlist_adapter (private var list: MutableList<CardData>): RecyclerView.Adapter<cardlist_adapter.ListItemViewHolder> () {

    inner class ListItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView!!) {
        var data_name: TextView = itemView!!.findViewById(R.id.data_name)
        var data_number: TextView = itemView!!.findViewById(R.id.data_number)
        var data_validity: TextView = itemView!!.findViewById(R.id.data_validity)

        fun bind(data: CardData, position: Int) {
            Log.d("cardlist_adapter", "===== ===== ===== ===== bind ===== ===== ===== =====")
            Log.d("cardlist_adapter", data.getName() + " " + data.getNumber() + " " + data.getValidity())
            data_name.text = data.getName()
            data_number.text = data.getNumber()
            data_validity.text = data.getValidity()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardlist_recycler, parent, false)
        return ListItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        Log.d("cardlist_adapter", "===== ===== ===== ===== onBindViewHolder ===== ===== ===== =====")
        holder.bind(list[position], position)

    }
}