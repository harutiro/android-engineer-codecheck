/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.yumemi.android.code_check.databinding.FragmentRepositoryDetailBinding
import jp.co.yumemi.android.code_check.databinding.FragmentRepositorySearchBinding
import kotlinx.coroutines.launch

class RepositorySearchFragment : Fragment(R.layout.fragment_repository_search) {

    private var binding: FragmentRepositorySearchBinding? = null
    private val _binding get() = binding!!

    private var viewModel: RepositorySearchViewModel? = null
    private val _viewModel get() = viewModel!!

    private val adapter =
        RepositoryListRecyclerViewAdapter(
            object : RepositoryListRecyclerViewAdapter.OnItemClickListener {
                override fun itemClick(repositoryItem: RepositoryItem) {
                    navigateToRepositoryFragment(repositoryItem)
                }
            },
        )

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRepositorySearchBinding.bind(view)
        viewModel = ViewModelProvider(this)[RepositorySearchViewModel::class.java]

        setupRecyclerView()
        setupSearchInput()
    }

    /**
     * RecyclerViewの初期化
     */
    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)

        _binding.recyclerView.apply {
            this.layoutManager = layoutManager
            addItemDecoration(dividerItemDecoration)
            adapter = this@RepositorySearchFragment.adapter
        }
    }

    /**
     * 検索入力のセットアップ
     */
    private fun setupSearchInput() {
        _binding.searchInputText.setOnEditorActionListener { editText, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                val query = editText.text.toString()
                performSearch(query)
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
                val results = _viewModel.fetchSearchResults(query)
                adapter.submitList(results)
            } catch (e: Exception) {
                // エラー処理を追加（例: ログの表示やUIへの通知）
            }
        }
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
