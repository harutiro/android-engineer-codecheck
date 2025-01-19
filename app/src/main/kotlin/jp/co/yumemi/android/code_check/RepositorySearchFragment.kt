/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.yumemi.android.code_check.databinding.FragmentRepositorySearchBinding
import kotlinx.coroutines.launch

class RepositorySearchFragment : Fragment(R.layout.fragment_repository_search) {
    private var _binding: FragmentRepositorySearchBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")

    private lateinit var viewModel: RepositorySearchViewModel

    private val adapter by lazy {
        RepositoryListRecyclerViewAdapter(
            object : RepositoryListRecyclerViewAdapter.OnItemClickListener {
                override fun itemClick(repositoryItem: RepositoryItem) {
                    navigateToRepositoryFragment(repositoryItem)
                }
            },
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRepositorySearchBinding.bind(view)
        viewModel = ViewModelProvider(this)[RepositorySearchViewModel::class.java]

        setupRecyclerView()
        setupSearchInput()
        hideErrorText()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }

    /**
     * RecyclerViewの初期化
     */
    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)

        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            addItemDecoration(dividerItemDecoration)
            adapter = this@RepositorySearchFragment.adapter
        }
    }

    /**
     * 検索入力のセットアップ
     */
    private fun setupSearchInput() {
        binding.searchInputText.setOnEditorActionListener { editText, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                val query = editText.text.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }
    }

    /**
     * 検索処理の実行
     */
    private fun performSearch(query: String) {
        lifecycleScope.launch {
            try {
                hideErrorText()
                val results = viewModel.fetchSearchResults(query)
                adapter.submitList(results)
            } catch (e: Exception) {
                Log.e("RepositorySearchFragment", "Search failed: $e")
                // ユーザー通知
                viewErrorText("検索を行えませんでした。")
            }
        }
    }

    private fun viewErrorText(
        message: String
    ) {
        binding.errorTextView.isEnabled = true
        binding.errorTextView.text = message
    }

    private fun hideErrorText() {
        binding.errorTextView.isEnabled = false
        binding.errorTextView.text = ""
    }

    /**
     * リポジトリ詳細画面への遷移
     */
    private fun navigateToRepositoryFragment(repositoryItem: RepositoryItem) {
        val action =
            RepositorySearchFragmentDirections
                .actionRepositoriesFragmentToRepositoryFragment(item = repositoryItem)
        findNavController().navigate(action)
    }
}
