package com.liteloapps.readwhat.ui.fragments
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.liteloapps.readwhat.R
import com.liteloapps.readwhat.ui.MainActivity
import com.liteloapps.readwhat.viewModel.NewsViewModel

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel

    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        val webView: WebView = view.findViewById(R.id.webView)

        val article = args.article

        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        val saveBtn : FloatingActionButton = view.findViewById(R.id.save_btn)
        saveBtn.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article Saved!!", Snackbar.LENGTH_SHORT).show()

        }
    }
}