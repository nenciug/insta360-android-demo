package com.insta360.kmpsdk.demo.ui.capture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.arashivision.sdk.camera.core.model.FunctionMode
import com.arashivision.sdk.camera.core.model.option.SensorMode
import com.insta360.kmpsdk.demo.R
import com.insta360.kmpsdk.demo.databinding.FragmentCaptureBinding
import com.insta360.kmpsdk.demo.ui.common.CameraDemoDisplayLabels
import com.insta360.kmpsdk.demo.ui.common.observeCameraDisconnectedNavigateToConnection
import com.insta360.kmpsdk.demo.ui.connection.ConnectionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Enhanced CaptureFragment supporting complete camera controls, localized UI labels,
 * real-time interaction locking, and advanced status updates.
 */
class CaptureFragment : Fragment() {
    private var _binding: FragmentCaptureBinding? = null
    private val binding get() = _binding!!

    private val connectionViewModel: ConnectionViewModel by activityViewModels {
        ConnectionViewModel.Factory(requireActivity().application)
    }

    private val captureViewModel: CameraCaptureViewModel by viewModels {
        CameraCaptureViewModel.Factory(requireActivity().application) {
            connectionViewModel.getCameraDevice()
        }
    }

    private var lastModeOptions: List<FunctionMode> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCaptureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        observeCameraDisconnectedNavigateToConnection(connectionViewModel)

        setupClickListeners()
        observeUiState()

        captureViewModel.onAppear()
    }

    private fun setupClickListeners() {
        binding.backLink.setOnClickListener { 
            findNavController().popBackStack() 
        }
        
        binding.openCaptureParamsSheet.setOnClickListener {
            CaptureParamsBottomSheetFragment.show(childFragmentManager)
        }
        
        binding.captureBtn.setOnClickListener {
            captureViewModel.onPrimaryCaptureButtonClicked()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                captureViewModel.ui.collectLatest { s ->
                    val blocking = s.busy || s.captureCommand != null
                    
                    // Update overlay and status
                    binding.blockingOverlay.isVisible = blocking
                    binding.blockingOverlay.setText(s.loadingText ?: "")
                    
                    // Lens controls
                    binding.lensSection.isVisible = s.lensRowVisible
                    rebuildLensChips(s.lensOptions, s.selectedLens)
                    
                    // Mode controls
                    rebuildModeChips(s.modeOptions, s.selectedMode)
                    
                    // Primary action button
                    binding.captureBtn.text = s.captureButtonLabel
                    binding.captureBtn.isEnabled = s.captureButtonEnabled && !blocking
                    
                    // Status indicators
                    binding.captureProgressLabel.text = s.progressLabel.orEmpty()
                    binding.captureProgressLabel.isVisible = !s.progressLabel.isNullOrEmpty()
                    
                    binding.captureSubStatusLabel.text = s.subStatusLabel.orEmpty()
                    binding.captureSubStatusLabel.isVisible = !s.subStatusLabel.isNullOrEmpty()
                    
                    // Result Summary
                    binding.resultPanel.isVisible = s.resultVisible
                    binding.resultSummary.text = s.resultSummary
                    
                    // Lock secondary UI controls when busy or capturing
                    applySecondaryInteractionLock(blocking || s.captureFlowActive)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            if (captureViewModel.isLowPowerDevice()) {
                captureViewModel.lockCameraScreen()
            }
        }
    }

    private fun rebuildLensChips(
        options: List<SensorMode>,
        selected: SensorMode?,
    ) {
        CaptureChips.render(
            container = binding.lensChipContainer,
            options = options,
            selected = selected,
            textSizeSp = 14f,
            label = { CameraDemoDisplayLabels.sensorMode(requireContext(), it) },
            onClick = { lens ->
                captureViewModel.selectLens(lens)
            },
        )
    }

    private fun rebuildModeChips(
        options: List<FunctionMode>,
        selected: FunctionMode?,
    ) {
        lastModeOptions =
            CaptureChips.renderStableModeStrip(
                scroll = binding.modeScroll,
                container = binding.modeChipContainer,
                previousOptions = lastModeOptions,
                options = options,
                selected = selected,
                label = { CameraDemoDisplayLabels.functionMode(requireContext(), it) },
                onClick = { mode ->
                    captureViewModel.selectMode(mode)
                },
            )
    }

    private fun applySecondaryInteractionLock(locked: Boolean) {
        val alpha = if (locked) 0.45f else 1.0f
        listOf(binding.backLink, binding.openCaptureParamsSheet).forEach { button ->
            button.isEnabled = !locked
            button.alpha = alpha
        }
        CaptureChips.setInteractionLocked(binding.lensChipContainer, locked)
        CaptureChips.setInteractionLocked(binding.modeChipContainer, locked)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
