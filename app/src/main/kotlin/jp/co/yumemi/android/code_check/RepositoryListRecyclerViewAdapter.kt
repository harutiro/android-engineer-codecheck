package jp.co.yumemi.android.code_check

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * DiffUtilの実装
 */
private val diffUtilCallback =
    object : DiffUtil.ItemCallback<RepositoryItem>() {
        override fun areItemsTheSame(
            oldItem: RepositoryItem,
            newItem: RepositoryItem,
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: RepositoryItem,
            newItem: RepositoryItem,
        ): Boolean {
            return oldItem == newItem
        }
    }

/**
 * RecyclerView Adapter
 */
class RepositoryListRecyclerViewAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<RepositoryItem, RepositoryListRecyclerViewAdapter.ViewHolder>(diffUtilCallback) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val repositoryNameView: TextView? = view.findViewById(R.id.repositoryNameView)

        /**
         * ビューにデータをバインド
         */
        fun bind(
            item: RepositoryItem,
            clickListener: OnItemClickListener,
        ) {
            repositoryNameView?.text = item.name
            itemView.setOnClickListener { clickListener.itemClick(item) }
        }
    }

    interface OnItemClickListener {
        fun itemClick(repositoryItem: RepositoryItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item, itemClickListener)
        } else {
            // アイテムがnullの場合の処理（必要なら追加）
        }
    }
}
