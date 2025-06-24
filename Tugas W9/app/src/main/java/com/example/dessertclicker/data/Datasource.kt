/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dessertclicker.data

import com.example.dessertclicker.R
import com.example.dessertclicker.model.Dessert

/**
 * [Datasource] generates a list of [Dessert]
 */
object Datasource {
    val dessertList = listOf(
        Dessert(R.drawable.cupcake, 5, 0),
        Dessert(R.drawable.donut, 10, 5),
        Dessert(R.drawable.eclair, 15, 10),
        Dessert(R.drawable.froyo, 30, 25),
        Dessert(R.drawable.gingerbread, 50, 50),
        Dessert(R.drawable.honeycomb, 100, 75),
        Dessert(R.drawable.icecreamsandwich, 500, 100),
        Dessert(R.drawable.jellybean, 1000, 150),
        Dessert(R.drawable.kitkat, 2000, 200),
        Dessert(R.drawable.lollipop, 3000, 300),
        Dessert(R.drawable.marshmallow, 4000, 400),
        Dessert(R.drawable.nougat, 5000, 500),
        Dessert(R.drawable.oreo, 6000, 600)
    )
}
