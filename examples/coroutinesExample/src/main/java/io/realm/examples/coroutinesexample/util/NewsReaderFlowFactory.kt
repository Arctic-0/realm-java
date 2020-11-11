/*
 * Copyright 2020 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.examples.coroutinesexample.util

import io.realm.*
import io.realm.coroutines.FlowFactory
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Similar to [io.realm.coroutines.RealmFlowFactory] but it will not emit the current value
 * immediately. This is needed by Store to function properly or else it will receive updates with
 * empty [RealmResults] that will make it think existing values for the current key are present.
 */
class NewsReaderFlowFactory : FlowFactory {

    override fun from(realm: Realm): Flow<Realm> {
        if (realm.isFrozen) {
            return flowOf(realm)
        }

        return callbackFlow {
            val flowRealm = Realm.getInstance(realm.configuration)
            val listener = RealmChangeListener<Realm> { listenerRealm ->
                offer(realm.freeze())
            }

            flowRealm.addChangeListener(listener)

            awaitClose {
                flowRealm.removeChangeListener(listener)
                flowRealm.close()
            }
        }
    }

    override fun from(dynamicRealm: DynamicRealm): Flow<DynamicRealm> {
        if (dynamicRealm.isFrozen) {
            return flowOf(dynamicRealm)
        }

        return callbackFlow {
            val flowRealm = DynamicRealm.getInstance(dynamicRealm.configuration)
            val listener = RealmChangeListener<DynamicRealm> { listenerRealm ->
                offer(dynamicRealm.freeze())
            }

            flowRealm.addChangeListener(listener)

            awaitClose {
                flowRealm.removeChangeListener(listener)
                flowRealm.close()
            }
        }
    }

    override fun <T : Any?> from(realm: Realm, results: RealmResults<T>): Flow<RealmResults<T>> {
        // Return "as is" if frozen, there will be no listening for changes
        if (realm.isFrozen) {
            return flowOf(results)
        }

        val config = realm.configuration

        return callbackFlow {
            // Do nothing if the results are invalid
            if (!results.isValid) {
                return@callbackFlow
            }

            // Get instance to ensure the Realm is open for as long as we are listening
            val flowRealm = Realm.getInstance(config)
            val listener = RealmChangeListener<RealmResults<T>> { listenerResults ->
                offer(listenerResults.freeze())
            }

            results.addChangeListener(listener)

            awaitClose {
                // Remove listener and cleanup
                if (!flowRealm.isClosed) {
                    results.removeChangeListener(listener)
                    flowRealm.close()
                }
            }
        }
    }

    override fun <T : Any?> from(dynamicRealm: DynamicRealm, results: RealmResults<T>): Flow<RealmResults<T>> {
        // Return "as is" if frozen, there will be no listening for changes
        if (dynamicRealm.isFrozen) {
            return flowOf(results)
        }

        val config = dynamicRealm.configuration

        return callbackFlow {
            // Do nothing if the results are invalid
            if (!results.isValid) {
                return@callbackFlow
            }

            // Get instance to ensure the Realm is open for as long as we are listening
            val flowRealm = DynamicRealm.getInstance(config)
            val listener = RealmChangeListener<RealmResults<T>> { listenerResults ->
                offer(listenerResults.freeze())
            }

            results.addChangeListener(listener)

            awaitClose {
                // Remove listener and cleanup
                if (!flowRealm.isClosed) {
                    results.removeChangeListener(listener)
                    flowRealm.close()
                }
            }
        }
    }

    override fun <T : Any?> from(realm: Realm, realmList: RealmList<T>): Flow<RealmList<T>> {
        // Return "as is" if frozen, there will be no listening for changes
        if (realm.isFrozen) {
            return flowOf(realmList)
        }

        val config = realm.configuration

        return callbackFlow {
            // Do nothing if the results are invalid
            if (!realmList.isValid) {
                return@callbackFlow
            }

            // Get instance to ensure the Realm is open for as long as we are listening
            val flowRealm = Realm.getInstance(config)
            val listener = RealmChangeListener<RealmList<T>> { listenerResults ->
                offer(listenerResults.freeze())
            }

            realmList.addChangeListener(listener)

            awaitClose {
                // Remove listener and cleanup
                if (!flowRealm.isClosed) {
                    realmList.removeChangeListener(listener)
                    flowRealm.close()
                }
            }
        }
    }

    override fun <T : Any?> from(dynamicRealm: DynamicRealm, realmList: RealmList<T>): Flow<RealmList<T>> {
        // Return "as is" if frozen, there will be no listening for changes
        if (dynamicRealm.isFrozen) {
            return flowOf(realmList)
        }

        val config = dynamicRealm.configuration

        return callbackFlow {
            // Do nothing if the results are invalid
            if (!realmList.isValid) {
                return@callbackFlow
            }

            // Get instance to ensure the Realm is open for as long as we are listening
            val flowRealm = DynamicRealm.getInstance(config)
            val listener = RealmChangeListener<RealmList<T>> { listenerResults ->
                offer(listenerResults.freeze())
            }

            realmList.addChangeListener(listener)

            awaitClose {
                // Remove listener and cleanup
                if (!flowRealm.isClosed) {
                    realmList.removeChangeListener(listener)
                    flowRealm.close()
                }
            }
        }
    }

    override fun <T : RealmModel?> from(realm: Realm, realmObject: T): Flow<T> {
        // Return "as is" if frozen, there will be no listening for changes
        if (realm.isFrozen) {
            return flowOf(realmObject)
        }

        val config = realm.configuration

        return callbackFlow<T> {
            // Do nothing if the object is invalid
            if (!RealmObject.isValid(realmObject)) {
                return@callbackFlow
            }

            // Get instance to ensure the Realm is open for as long as we are listening
            val flowRealm = Realm.getInstance(config)
            val listener = RealmChangeListener<T> { listenerObj ->
                offer(RealmObject.freeze(listenerObj) as T)
            }

            RealmObject.addChangeListener(realmObject, listener)

            awaitClose {
                // Remove listener and cleanup
                if (!flowRealm.isClosed) {
                    RealmObject.removeChangeListener(realmObject, listener)
                    flowRealm.close()
                }
            }
        }
    }

    override fun from(dynamicRealm: DynamicRealm, dynamicRealmObject: DynamicRealmObject): Flow<DynamicRealmObject> {
        // Return "as is" if frozen, there will be no listening for changes
        if (dynamicRealm.isFrozen) {
            return flowOf(dynamicRealmObject)
        }

        val config = dynamicRealm.configuration

        return callbackFlow<DynamicRealmObject> {
            // Do nothing if the object is invalid
            if (!dynamicRealmObject.isValid) {
                return@callbackFlow
            }

            // Get instance to ensure the Realm is open for as long as we are listening
            val flowRealm = DynamicRealm.getInstance(config)
            val listener = RealmChangeListener<DynamicRealmObject> { listenerObj ->
                offer(listenerObj.freeze())
            }

            dynamicRealmObject.addChangeListener(listener)

            awaitClose {
                // Remove listener and cleanup
                if (!flowRealm.isClosed) {
                    dynamicRealmObject.removeChangeListener(listener)
                    flowRealm.close()
                }
            }
        }
    }
}
