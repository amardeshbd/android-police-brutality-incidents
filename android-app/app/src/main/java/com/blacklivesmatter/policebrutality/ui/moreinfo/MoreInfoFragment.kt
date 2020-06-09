package com.blacklivesmatter.policebrutality.ui.moreinfo

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.blacklivesmatter.policebrutality.R
import com.blacklivesmatter.policebrutality.databinding.FragmentMoreInfoBinding
import com.blacklivesmatter.policebrutality.ui.extensions.observeKotlin
import com.blacklivesmatter.policebrutality.ui.util.IntentBuilder
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

class MoreInfoFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MoreInfoViewModel> { viewModelFactory }
    private lateinit var viewDataBinding: FragmentMoreInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = FragmentMoreInfoBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@MoreInfoFragment
            vm = viewModel
        }

        (requireActivity() as AppCompatActivity).setSupportActionBar(viewDataBinding.toolbar)

        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupHashTagHandler()
        handleExternalUrl()
    }

    private fun handleExternalUrl() {
        viewModel.openExternalUrl.observeKotlin(viewLifecycleOwner) { url ->
            val intent = IntentBuilder.build(requireContext(), url)
            if (intent != null) {
                startActivity(intent)
            } else {
                Snackbar.make(viewDataBinding.root, R.string.unable_to_load_url, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupHashTagHandler() {
        viewDataBinding.content.popularHashTagsContainer.children.forEach { childChipView ->
            if (childChipView is Chip) {
                childChipView.setOnClickListener(chipClickListener)
            }
        }
    }

    /**
     * Internal listener to handle clicks from existing chips.
     */
    private val chipClickListener = View.OnClickListener { view ->
        if (view is Chip) {
            copyTextToClipboard(view.text.toString())
        }
    }

    private fun copyTextToClipboard(copyText: String) {
        val clipboardManager: ClipboardManager =
            ContextCompat.getSystemService(requireContext(), ClipboardManager::class.java)!!
        val clipData = ClipData.newPlainText("text", copyText)
        clipboardManager.setPrimaryClip(clipData)

        Snackbar.make(
            viewDataBinding.root,
            resources.getString(R.string.message_hashtag_copied_to_clipboard, copyText),
            Snackbar.LENGTH_LONG
        ).show()
    }

    //
    // Handle menu icons for about app and share app
    //
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.more_info_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_menu_about_app -> {
                Timber.d("About app menu item selected.")
                showAboutAppDialog()
                return true
            }
            R.id.toolbar_menu_share -> {
                Timber.d("Share app menu item selected.")
                // TODO - update this whenever app is published at
                // https://play.google.com/store/apps/details?id=com.blacklivesmatter.policebrutality
                Snackbar.make(
                    viewDataBinding.root,
                    "Sharing coming soon: This app is pending approval on Google Play Store. " +
                            "Thanks for caring! ❤️",
                    Snackbar.LENGTH_LONG
                ).show()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showAboutAppDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setPositiveButton("OK", null)
            .setNegativeButton("Nah", null)
            .setView(R.layout.dialog_about_app)
            .show()
    }
}
