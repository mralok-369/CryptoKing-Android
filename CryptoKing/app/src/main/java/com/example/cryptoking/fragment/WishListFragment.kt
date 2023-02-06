package com.example.cryptoking.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.cryptoking.R
import com.example.cryptoking.adapter.MarketAdapter
import com.example.cryptoking.apis.ApiInterface
import com.example.cryptoking.apis.ApiUtilities
import com.example.cryptoking.databinding.FragmentWishListBinding
import com.example.cryptoking.models.CryptoCurrency
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WishListFragment : Fragment() {

    private lateinit var binding : FragmentWishListBinding
    private lateinit var wishList : ArrayList<String>
    private lateinit var wishListItem : ArrayList<CryptoCurrency>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWishListBinding.inflate(layoutInflater)

        readData()

        lifecycleScope.launch(Dispatchers.IO){
            val res = ApiUtilities.getInstance().create(ApiInterface::class.java)
                .getMarketData()

            if (res.body() != null){
                withContext(Dispatchers.Main){
                    wishListItem = ArrayList()
                    wishListItem.clear()

                    for (watchData in wishList){
                        for (item in res.body()!!.data.cryptoCurrencyList){
                            if (watchData == item.symbol){
                                wishListItem.add(item)
                            }
                        }
                    }
                    binding.spinKitView.visibility = GONE
                    binding.watchlistRecyclerView.adapter = MarketAdapter(requireContext(), wishListItem, "watchfragment")
                }
            }
        }

        return binding.root
    }

    private fun readData() {
        val sharePreferences = requireContext().getSharedPreferences("wishlist", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharePreferences.getString("wishlist", ArrayList<String>().toString())
        val type = object : TypeToken<ArrayList<String>>(){}.type
        wishList = gson.fromJson(json, type)
    }


}