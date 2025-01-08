/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import jp.co.yumemi.android.code_check.databinding.FragmentRepositoryDetailBinding

class RepositoryDetailFragment : Fragment(R.layout.fragment_repository_detail) {
    private val args: RepositoryDetailFragmentArgs by navArgs()

    private var binding: FragmentRepositoryDetailBinding? = null
    private val _binding get() = binding!!

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", lastSearchDate.toString())

        binding = FragmentRepositoryDetailBinding.bind(view)

        var item = args.item

        _binding.ownerIconView.load(item.ownerIconUrl)
        _binding.nameView.text = item.name
        _binding.languageView.text = item.language
        _binding.starsView.text = resources.getString(R.string.stars_count, item.stargazersCount)
        _binding.watchersView.text = resources.getString(R.string.watchers_count, item.watchersCount)
        _binding.forksView.text = resources.getString(R.string.forks_count, item.forksCount)
        _binding.openIssuesView.text = resources.getString(R.string.open_issues_count, item.openIssuesCount)
    }
}
