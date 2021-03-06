/*
 *             Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2017 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package org.mariotaku.twidere.extension.model.api.microblog

import org.mariotaku.microblog.library.twitter.model.Relationship
import org.mariotaku.twidere.model.ParcelableRelationship
import org.mariotaku.twidere.model.UserKey


fun Relationship.toParcelable(accountKey: UserKey, userKey: UserKey, filtering: Boolean = false):
        ParcelableRelationship {
    val obj = ParcelableRelationship()
    obj.account_key = accountKey
    obj.user_key = userKey
    obj.following = isSourceFollowingTarget
    obj.followed_by = isSourceFollowedByTarget
    obj.blocking = isSourceBlockingTarget
    obj.blocked_by = isSourceBlockedByTarget
    obj.muting = isSourceMutingTarget
    obj.retweet_enabled = isSourceWantRetweetsFromTarget
    obj.notifications_enabled = isSourceNotificationsEnabledForTarget
    obj.can_dm = canSourceDMTarget()
    obj.filtering = filtering
    return obj
}
