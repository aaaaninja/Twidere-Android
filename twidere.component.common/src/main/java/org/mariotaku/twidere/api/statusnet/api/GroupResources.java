package org.mariotaku.twidere.api.statusnet.api;

import org.mariotaku.restfu.annotation.method.GET;
import org.mariotaku.restfu.annotation.param.Path;
import org.mariotaku.restfu.annotation.param.Query;
import org.mariotaku.twidere.api.statusnet.model.Group;
import org.mariotaku.twidere.api.twitter.TwitterException;
import org.mariotaku.twidere.api.twitter.model.Paging;
import org.mariotaku.twidere.api.twitter.model.ResponseList;
import org.mariotaku.twidere.api.twitter.model.Status;
import org.mariotaku.twidere.api.twitter.model.User;

/**
 * Created by mariotaku on 16/3/4.
 */
public interface GroupResources {

    @GET("/statusnet/groups/timeline/{group_id}.json")
    ResponseList<Status> getGroupStatuses(@Path("group_id") long groupId, @Query Paging paging) throws TwitterException;

    @GET("/statusnet/groups/timeline/{group_name}.json")
    ResponseList<Status> getGroupStatuses(@Path("group_name") String name, @Query Paging paging) throws TwitterException;

    @GET("/statusnet/groups/show.json")
    Group showGroup(@Query("group_id") long groupId) throws TwitterException;

    @GET("/statusnet/groups/show.json")
    Group showGroup(@Query("group_name") String groupName) throws TwitterException;

    @GET("/statusnet/groups/membership.json")
    ResponseList<User> getGroupMembers(@Query("group_id") long groupId) throws TwitterException;

    @GET("/statusnet/groups/membership.json")
    ResponseList<User> getGroupMembers(@Query("group_name") String groupName) throws TwitterException;

    @GET("/statusnet/groups/list.json")
    ResponseList<Group> getGroupsByScreenName(@Query("screen_name") String screenName) throws TwitterException;

    @GET("/statusnet/groups/list.json")
    ResponseList<Group> getGroups(@Query("id") String userId) throws TwitterException;

    @GET("/statusnet/groups/list_all.json")
    ResponseList<Group> getAllGroups(@Query Paging paging) throws TwitterException;

}
