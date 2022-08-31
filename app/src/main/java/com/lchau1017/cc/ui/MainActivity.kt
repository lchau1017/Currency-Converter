package com.lchau1017.cc.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lchau1017.cc.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
    }

}