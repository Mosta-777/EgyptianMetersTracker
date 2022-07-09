package com.mostapps.egyptianmeterstracker.screens.details.meterreadingscollectionslist

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.mostapps.egyptianmeterstracker.GlideApp
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseFragment
import com.mostapps.egyptianmeterstracker.databinding.FragmentMeterReadingCollectionsListBinding
import com.mostapps.egyptianmeterstracker.models.MeterReadingsCollectionListItem
import com.mostapps.egyptianmeterstracker.screens.details.meterdetails.MeterDetailsViewModel
import com.mostapps.egyptianmeterstracker.utils.setDisplayHomeAsUpEnabled
import com.mostapps.egyptianmeterstracker.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MeterReadingsCollectionsListFragment : BaseFragment() {


    override val _viewModel: MeterReadingsCollectionsListViewModel by viewModel()
    private val parentViewModel: MeterDetailsViewModel by sharedViewModel()
    private val cameraPermissionCode = 1000;
    private val imageCaptureCode = 1001
    private var imageUri: Uri? = null


    private lateinit var binding: FragmentMeterReadingCollectionsListBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_meter_reading_collections_list, container, false
            )
        binding.viewModel = _viewModel

        binding.meterImageView.setOnClickListener {
            val permissionGranted = requestCameraPermission()
            if (permissionGranted) {
                openCameraInterface()
            }
        }

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)


        parentViewModel.meter.observe(viewLifecycleOwner) { meter ->
            if (meter != null) {
                _viewModel.setSelectedMeter(meter)
                val storageReference = _viewModel.getMeterImageStorageReference()

                storageReference?.downloadUrl?.addOnSuccessListener {
                    GlideApp.with(this)
                        .load(storageReference)
                        .error(R.drawable.no_photo)
                        .placeholder(R.drawable.no_photo)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(binding.meterImageView)
                }?.addOnFailureListener {
                    //TODO Handle No Meter image yet
                }
            }
        }


        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
    }


    private fun setupRecyclerView() {
        val adapter =
            MeterReadingsCollectionListAdapter { _: MeterReadingsCollectionListItem, position: Int ->
                _viewModel.handleOnMeterCollectionClicked(position)
            }
        binding.meterCollectionsRecyclerView.setup(adapter)
    }


    private fun requestCameraPermission(): Boolean {
        var permissionGranted = false
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            val cameraPermissionNotGranted = checkSelfPermission(
                activity as Context,
                Manifest.permission.CAMERA
            ) == PERMISSION_DENIED
            if (cameraPermissionNotGranted) {
                val permission = arrayOf(Manifest.permission.CAMERA)
                requestPermissions(permission, cameraPermissionCode)
            } else {
                permissionGranted = true
            }
        } else {
            permissionGranted = true
        }
        return permissionGranted
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode === cameraPermissionCode) {
            if (grantResults.size === 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraInterface()
            } else {
                showAlert(getString(R.string.error_camera_permission_denied))
            }
        }
    }

    private fun openCameraInterface() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, R.string.take_meter_picture)
        values.put(MediaStore.Images.Media.DESCRIPTION, R.string.take_meter_picture_description)
        imageUri =
            activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, imageCaptureCode)
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(activity as Context)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.ok, null)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            binding.meterImageView.setImageURI(imageUri)
            _viewModel.startMeterImageUpload(imageUri)
        } else {
            showAlert(getString(R.string.error_taking_photo))
        }
    }


}