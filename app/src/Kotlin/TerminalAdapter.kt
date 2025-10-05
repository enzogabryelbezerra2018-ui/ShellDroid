package com.shelldroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TerminalAdapter(private val items: MutableList<TerminalLine>) :
    RecyclerView.Adapter<TerminalAdapter.VH>() {

    class VH(item: View) : RecyclerView.ViewHolder(item) {
        val tvCommand: TextView = item.findViewById(R.id.tvCommand)
        val tvOutput: TextView = item.findViewById(R.id.tvOutput)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_line, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val line = items[position]
        holder.tvCommand.text = "$ ${line.command}"
        holder.tvOutput.text = line.output
    }

    override fun getItemCount(): Int = items.size

    fun addLine(line: TerminalLine) {
        items.add(line)
        notifyItemInserted(items.size - 1)
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun getAll(): List<TerminalLine> = items.toList()
}
