package com.github.policebrutality.ui.incident

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.github.policebrutality.R
import com.github.policebrutality.databinding.ListItemIncidentExternalLinkBinding
import com.github.policebrutality.ui.common.DataBoundListAdapter
import com.github.policebrutality.ui.util.LinkTransformer


class IncidentLinkAdapter(
    private val itemClickCallback: ((String) -> Unit)?
) : DataBoundListAdapter<String, ListItemIncidentExternalLinkBinding>(
    diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun createBinding(parent: ViewGroup): ListItemIncidentExternalLinkBinding {
        val binding = DataBindingUtil.inflate<ListItemIncidentExternalLinkBinding>(
            LayoutInflater.from(parent.context), R.layout.list_item_incident_external_link,
            parent, false
        )

        binding.root.setOnClickListener {
            binding.link?.let {
                itemClickCallback?.invoke(it.name)
            }
        }
        return binding
    }

    override fun bind(binding: ListItemIncidentExternalLinkBinding, item: String) {
        val linkInfo = LinkTransformer.toLinkInfo(item)
        binding.link = linkInfo

        binding.button.setIconResource(linkInfo.iconResId)
    }
}