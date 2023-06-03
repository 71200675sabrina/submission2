package com.example.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.databinding.ItemLoadBinding

class LoadAdapter (private val retry: () -> Unit) : androidx.paging.LoadStateAdapter<LoadAdapter.LoadAdapterViewHolder> () {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadAdapter.LoadAdapterViewHolder {
        val binding = ItemLoadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadAdapterViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadAdapter.LoadAdapterViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadAdapterViewHolder(private val binding: ItemLoadBinding, retry: () -> Unit) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.btnreload.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error){
                binding.infoError.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.btnreload.isVisible = loadState is LoadState.Error
            binding.infoError.isVisible = loadState is LoadState.Error
        }
    }
}