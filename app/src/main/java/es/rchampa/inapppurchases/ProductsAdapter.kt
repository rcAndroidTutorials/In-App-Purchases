package es.rchampa.inapppurchases

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.android.billingclient.api.SkuDetails

class ProductsAdapter(
    private val list: List<SkuDetails>,
    private val onProductClicked: (SkuDetails) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {


    private val testList = listOf("android.test.purchased", "android.test.canceled", "android.test.item_unavailable")

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false) as TextView
        val viewHolder = ViewHolder(textView)
        textView.setOnClickListener { onProductClicked(list[viewHolder.adapterPosition]) }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(list[position].sku in testList){
            val index = list[position].sku.indexOfLast { it=='.' }.plus(1)
            holder.textView.text = list[position].sku.substring(index)+" "+list[position].title + " " + list[position].price
        }
        else{
            holder.textView.text = list[position].title + " " + list[position].price
        }

    }

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
}