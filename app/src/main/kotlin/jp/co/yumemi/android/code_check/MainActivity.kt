/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.core.presenter.MainScreen
import jp.co.yumemi.android.code_check.core.presenter.theme.CodeCheckAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeCheckAppTheme {
                MainScreen()
            }
        }
    }
}
