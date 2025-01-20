package jp.co.yumemi.android.code_check.core.presenter.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.core.entity.RepositoryItem
import jp.co.yumemi.android.code_check.core.utils.DialogHelper
import jp.co.yumemi.android.code_check.databinding.FragmentRepositorySearchBinding

@AndroidEntryPoint
class RepositorySearchFragment : Fragment(R.layout.fragment_repository_search) {
    private var _binding: FragmentRepositorySearchBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    private val viewModel: RepositorySearchViewModel by viewModels()

    private val adapter by lazy {
        RepositoryListRecyclerViewAdapter(
            object : RepositoryListRecyclerViewAdapter.OnItemClickListener {
                override fun itemClick(repositoryItem: RepositoryItem) {
                    onItemClick(repositoryItem)
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

        observeViewModel()
        setupRecyclerView()
        setupSearchInput()
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            it?.let {
                DialogHelper.showErrorDialog(
                    requireContext(),
                    requireContext().getString(it),
                )
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), layoutManager.orientation)

        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            addItemDecoration(dividerItemDecoration)
            adapter = this@RepositorySearchFragment.adapter
        }
    }

    private fun setupSearchInput() {
        binding.searchInputText.setOnEditorActionListener { editText, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchRepositories(editText.text.toString().trim())
                true
            } else {
                false
            }
        }
    }

    /**
     * リポジトリ検索結果のクリックイベント
     * リサイクラービューでアイテムが押された時に動作を行います。
     */
    private fun onItemClick(item: RepositoryItem) {
        val action =
            RepositorySearchFragmentDirections
                .actionRepositoriesFragmentToRepositoryFragment(item = item)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
