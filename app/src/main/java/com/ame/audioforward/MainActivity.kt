package com.ame.audioforward

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.updateLayoutParams
import com.ame.audioforward.core.KEY_IP
import com.ame.audioforward.core.SP_IP
import com.ame.audioforward.databinding.ActivityMainBinding
import com.ame.audioforward.service.AudioForwardService
import com.ame.audioforward.util.IPTools
import com.ame.audioforward.util.SPUtil

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val ivLogo: ImageView by lazy { binding.ivLogo }
    private val etIp: EditText by lazy { binding.etIp }
    private val btnConnect: Button by lazy { binding.btnConnect }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            // EdgeToEdge behavior
            setStatusBarTintByUIMode()
            ivLogo.setOnApplyWindowInsetsListener { v, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsets.Type.statusBars())
                // Apply the insets as a margin to the view. This solution sets
                // only the bottom, left, and right dimensions, but you can apply whichever
                // insets are appropriate to your layout. You can also update the view padding
                // if that's more appropriate.
                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = insets.top
                }

                // Return CONSUMED if you don't want want the window insets to keep passing
                // down to descendant views.
                WindowInsets.CONSUMED
            }
        }

        etIp.setText(SPUtil.get(SP_IP, "")) // Restore the last inputted ip

        btnConnect.apply {
            if (AudioForwardService.isRunning()) {
                text = getString(R.string.btn_disconnect_text)
            }
            setOnClickListener {
                if (!AudioForwardService.isRunning()) {
                    val ip = etIp.text.toString()
                    if (IPTools.isValidIpv4Address(ip)) {
                        val intent =
                            Intent(
                                this@MainActivity.applicationContext,
                                AudioForwardService::class.java,
                            )
                        intent.putExtra(KEY_IP, ip)
                        startForegroundService(intent)
                        text = getString(R.string.btn_disconnect_text)
                        SPUtil.put(SP_IP, ip) // Record the last inputted ip
                    } else {
                        Toast
                            .makeText(
                                this@MainActivity,
                                R.string.tip_invalid_ip,
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                } else {
                    val intent =
                        Intent(
                            this@MainActivity.applicationContext,
                            AudioForwardService::class.java,
                        )
                    AudioForwardService.stopService()
                    stopService(intent)
                    text = getString(R.string.btn_connect_text)
                }
            }
        }
    }

    private fun setStatusBarTintByUIMode() {
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val isDarkMode = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
        WindowCompat
            .getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = !isDarkMode
    }
}
