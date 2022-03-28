package com.mostapps.egyptianmeterstracker.base

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment() {
    abstract val _viewModel: BaseViewModel

    override fun onStart() {
        super.onStart()
        _viewModel.showErrorMessage.observe(this) { errorString: String ->
            Toast.makeText(activity, errorString, Toast.LENGTH_LONG).show()
        }
        _viewModel.showToast.observe(this) { textToShow: String ->
            Toast.makeText(activity, textToShow, Toast.LENGTH_LONG).show()
        }
        _viewModel.showSnackBar.observe(this) { snackBarText: String ->
            Snackbar.make(this.requireView(), snackBarText, Snackbar.LENGTH_LONG).show()
        }
        _viewModel.showSnackBarInt.observe(this, Observer {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        })

        _viewModel.navigationCommand.observe(this) { command: NavigationCommand ->
            when (command) {
                is NavigationCommand.To -> findNavController().navigate(command.directions)
                is NavigationCommand.Back -> findNavController().popBackStack()
                is NavigationCommand.BackTo -> findNavController().popBackStack(
                    command.destinationId,
                    false
                )
            }
        }
    }
}