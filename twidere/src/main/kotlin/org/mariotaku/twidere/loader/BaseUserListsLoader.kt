/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.loader

import android.accounts.AccountManager
import android.content.Context
import android.support.v4.content.FixedAsyncTaskLoader
import android.util.Log
import org.mariotaku.kpreferences.get
import org.mariotaku.microblog.library.MicroBlog
import org.mariotaku.microblog.library.MicroBlogException
import org.mariotaku.microblog.library.twitter.model.CursorSupport
import org.mariotaku.microblog.library.twitter.model.PageableResponseList
import org.mariotaku.microblog.library.twitter.model.Paging
import org.mariotaku.microblog.library.twitter.model.UserList
import org.mariotaku.twidere.R
import org.mariotaku.twidere.TwidereConstants.LOGTAG
import org.mariotaku.twidere.constant.loadItemLimitKey
import org.mariotaku.twidere.extension.model.newMicroBlogInstance
import org.mariotaku.twidere.loader.iface.ICursorSupportLoader
import org.mariotaku.twidere.model.ParcelableUserList
import org.mariotaku.twidere.model.UserKey
import org.mariotaku.twidere.model.util.AccountUtils
import org.mariotaku.twidere.model.util.ParcelableUserListUtils
import org.mariotaku.twidere.util.SharedPreferencesWrapper
import org.mariotaku.twidere.util.collection.NoDuplicatesArrayList
import org.mariotaku.twidere.util.dagger.GeneralComponentHelper
import java.util.*
import javax.inject.Inject


abstract class BaseUserListsLoader(
        context: Context,
        protected val accountKey: UserKey?,
        override val cursor: Long,
        data: List<ParcelableUserList>?
) : FixedAsyncTaskLoader<List<ParcelableUserList>>(context), ICursorSupportLoader {
    @Inject
    lateinit var preferences: SharedPreferencesWrapper

    protected val data = NoDuplicatesArrayList<ParcelableUserList>()

    private val profileImageSize = context.getString(R.string.profile_image_size)

    override var nextCursor: Long = 0
    override var prevCursor: Long = 0

    init {
        GeneralComponentHelper.build(context).inject(this)
        if (data != null) {
            this.data.addAll(data)
        }
    }

    @Throws(MicroBlogException::class)
    abstract fun getUserLists(twitter: MicroBlog, paging: Paging): List<UserList>

    override fun loadInBackground(): List<ParcelableUserList> {
        if (accountKey == null) return emptyList()
        var listLoaded: List<UserList>? = null
        try {
            val am = AccountManager.get(context)
            val details = AccountUtils.getAccountDetails(am, accountKey, true) ?: return data
            val twitter = details.newMicroBlogInstance(context, MicroBlog::class.java)
            val paging = Paging()
            paging.count(preferences[loadItemLimitKey].coerceIn(0, 100))
            if (cursor > 0) {
                paging.cursor(cursor)
            }
            listLoaded = getUserLists(twitter, paging)
        } catch (e: MicroBlogException) {
            Log.w(LOGTAG, e)
        }

        if (listLoaded != null) {
            val listSize = listLoaded.size
            if (listLoaded is PageableResponseList<*>) {
                nextCursor = (listLoaded as CursorSupport).nextCursor
                prevCursor = listLoaded.previousCursor
                val dataSize = data.size
                for (i in 0..listSize - 1) {
                    val list = listLoaded[i]
                    data.add(ParcelableUserListUtils.from(list, accountKey, (dataSize + i).toLong(),
                            isFollowing(list), profileImageSize))
                }
            } else {
                for (i in 0..listSize - 1) {
                    val list = listLoaded[i]
                    data.add(ParcelableUserListUtils.from(listLoaded[i], accountKey, i.toLong(),
                            isFollowing(list), profileImageSize))
                }
            }
        }
        Collections.sort(data)
        return data
    }

    override fun onStartLoading() {
        forceLoad()
    }

    protected open fun isFollowing(list: UserList): Boolean {
        return list.isFollowing
    }
}
