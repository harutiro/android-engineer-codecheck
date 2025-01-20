/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.core.presenter.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.core.entity.RepositoryEntity
import jp.co.yumemi.android.code_check.databinding.FragmentRepositoryDetailBinding

@AndroidEntryPoint
class RepositoryDetailFragment : Fragment(R.layout.fragment_repository_detail) {
    private val args: RepositoryDetailFragmentArgs by navArgs()

    private var _binding: FragmentRepositoryDetailBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentRepositoryDetailBinding.bind(view)

        val item = args.item
        bindViews(item)
    }

    private fun bindViews(item: RepositoryEntity) {
        binding.ownerIconView.load(item.ownerIconUrl)
        binding.nameView.text = item.name
        binding.languageView.text = resources.getString(R.string.written_language, item.language)
        binding.starsView.text = resources.getString(R.string.stars_count, item.stargazersCount)
        binding.watchersView.text = resources.getString(R.string.watchers_count, item.watchersCount)
        binding.forksView.text = resources.getString(R.string.forks_count, item.forksCount)
        binding.openIssuesView.text = resources.getString(R.string.open_issues_count, item.openIssuesCount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
