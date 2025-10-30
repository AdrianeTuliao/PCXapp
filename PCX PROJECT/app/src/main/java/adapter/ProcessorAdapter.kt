package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pcxlogin.R
import model.Processor

class ProcessorAdapter(private val processorList: List<Processor>) :
    RecyclerView.Adapter<ProcessorAdapter.ProcessorViewHolder>() {

    class ProcessorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val processorName: TextView = itemView.findViewById(R.id.IntelCorei7)
        val processorPrice: TextView = itemView.findViewById(R.id.IntelCorei7Price)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_processor, parent, false)
        return ProcessorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProcessorViewHolder, position: Int) {
        val processor = processorList[position]
        holder.processorName.text = processor.name
        holder.processorPrice.text = processor.price
    }

    override fun getItemCount(): Int = processorList.size
}
