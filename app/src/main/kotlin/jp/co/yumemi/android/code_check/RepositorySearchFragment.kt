/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.app.AlertDialog
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
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[RepositorySearchViewModel::class.java]

        // エラーメッセージを監視
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                showErrorDialog(it) // 必要に応じてダイアログやToastを表示
            }
        }

        setupRecyclerView()
        setupSearchInput()
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
                val results = viewModel.fetchSearchResults(query)
                adapter.submitList(results)
            } catch (e: Exception) {
                Log.e("RepositorySearchFragment", "Search failed: $e")
                // ユーザー通知
                showErrorDialog("検索に失敗しました。")
            }
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("エラー")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
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
