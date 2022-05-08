package com.example.mvi_architecture_android.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvi_architecture_android.R
import com.example.mvi_architecture_android.data.api.ApiHelperImpl
import com.example.mvi_architecture_android.data.model.User
import com.example.mvi_architecture_android.databinding.ActivityMainBinding
import com.example.mvi_architecture_android.ui.adapter.MainAdapter
import com.example.mvi_architecture_android.ui.intent.MainIntent
import com.example.mvi_architecture_android.ui.viewModel.MainViewModel
import com.example.mvi_architecture_android.ui.viewModel.ViewModelFactory
import com.example.mvi_architecture_android.ui.viewState.MainState
import com.example.mvi_architecture_android.util.RetrofitBuilder
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private val mainViewModel: MainViewModel by viewModels(
        factoryProducer = {
            ViewModelFactory(ApiHelperImpl(
                RetrofitBuilder.apiService
            ))
        }
    )

    private var adapter = MainAdapter(arrayListOf())
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this , R.layout.activity_main)
        setupUI()
        observeViewModel()
        setupClicks()
    }

    private fun setupClicks() {
        binding?.buttonFetchUser?.setOnClickListener {
            lifecycleScope.launch {
                mainViewModel.userIntent.send(MainIntent.FetchUser)
            }
        }
    }

    private fun setupUI() {
        binding?.recyclerView?.layoutManager = LinearLayoutManager(this)
        binding?.recyclerView?.run {
            addItemDecoration(
                DividerItemDecoration(
                    binding?.recyclerView?.context,
                    (binding?.recyclerView?.layoutManager as LinearLayoutManager).orientation
                )
            )
        }
        binding?.recyclerView?.adapter = adapter
    }

    private fun observeViewModel() {
        binding?.apply {
            lifecycleScope.launch {
                mainViewModel.state.collect {
                    when (it) {
                        is MainState.Idle -> {

                        }
                        is MainState.Loading -> {
                            buttonFetchUser.visibility = View.GONE
                            progressBar.visibility = View.VISIBLE
                        }

                        is MainState.Users -> {
                            progressBar.visibility = View.GONE
                            buttonFetchUser.visibility = View.GONE
                            renderList(it.user)
                        }
                        is MainState.Error -> {
                            progressBar.visibility = View.GONE
                            buttonFetchUser.visibility = View.VISIBLE
                            Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun renderList(users: List<User>) {
        binding?.recyclerView?.visibility = View.VISIBLE
        users.let { listOfUsers ->
            listOfUsers.let {
                adapter.addData(it)
                adapter.notifyDataSetChanged()
            }
        }
    }



}