package com.app.meetingpoint.helpers;

public class Constants {
    public static final String PREFERENCE_NAME_KEY = "meetingPointPreference";
    public static final String SIGNED_IN_KEY = "signedIn";
    public static final String SEPERATOR = "ùùùùùùùùùùù";


    // USERS TABLE
    public static final String COLLECTION_USERS_KEY = "users";
    public static final String ATTRIBUTE_USERS_EMAIL_KEY = "email";
    public static final String ATTRIBUTE_USERS_USERNAME_KEY = "username";
    public static final String ATTRIBUTE_USERS_PASSWORD_KEY = "password";
    public static final String ATTRIBUTE_USERS_ID_KEY = "userId";
    public static final String ATTRIBUTE_USERS_FCM_TOKEN_KEY = "fcmToken";
    public static final String ATTRIBUTE_USERS_IMAGE_KEY = "image";
    public static final String ATTRIBUTE_USERS_RATING_KEY = "rating";
    public static final String ATTRIBUTE_USERS_GROUPS_KEY = "groups";
    public static final String ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY = "favoriteTopics";
    public static final String ATTRIBUTE_USERS_ADDRESS_KEY = "address";
    public static final String ATTRIBUTE_USERS_EDUCATION_KEY = "education";
    public static final String ATTRIBUTE_USERS_WORK_KEY = "work";
    public static final String ATTRIBUTE_USERS_BACK_IMAGE_KEY = "backImage";
    public static final String ATTRIBUTR_USERS_AVAILABLE_KEY = "available";


    //GROUP TABLE
    public static final String COLLECTION_GROUP_KEY = "group";
    public static final String ATTRIBUTE_GROUP_ADMIN_KEY = "adminId";
    public static final String ATTRIBUTE_GROUP_ID_KEY = "groupId";
    public static final String ATTRIBUTE_GROUP_NAME_KEY = "groupName";
    public static final String ATTRIBUTE_GROUP_IMAGE_KEY = "groupImage";
    public static final String ATTRIBUTE_GROUP_BACK_IMAGE_KEY = "backImage";
    public static final String ATTRIBUTE_GROUP_RATING_KEY = "groupRating";
    public static final String ATTRIBUTE_GROUP_TOPIC_KEY = "topic";
    public static final String ATTRIBUTE_GROUP_NOUSERS_KEY = "nousers";
    public static final String ATTRIBUTE_GROUP_CONVERSATION_ID_KEY = "conversationId";
    public static final String ATTRIBUTE_GROUP_DESCRIPTION_KEY = "description";

    //TOPICS TABLE
    public static final String COLLECTION_TOPICS_KEY = "topics";
    public static final String ATTRIBUTE_TOPICS_ID_KEY = "topicId";
    public static final String ATTRIBUTE_TOPICS_TOPIC_KEY = "topic";

    //POST TABLE
    public static final String COLLECTION_POST_KEY = "post";
    public static final String ATTRIBUTE_POST_ID_KEY = "postId";
    public static final String ATTRIBUTE_POST_GROUP_ID_KEY = "groupId";
    public static final String ATTRIBUTE_POST_POSTER_NAME_KEY = "posterName";
    public static final String ATTRIBUTE_POST_GROUP_NAME_KEY = "groupName";
    public static final String ATTRIBUTE_POST_POSTER_ID_KEY = "posterId";
    public static final String ATTRIBUTE_POST_POSTER_IMAGE_KEY = "posterImage";
    public static final String ATTRIBUTE_POST_DATE_KEY = "postDate";
    public static final String ATTRIBUTE_POST_VISIBILITY_KEY = "postVisibility";
    public static final String ATTRIBUTE_POST_CAPTION_KEY = "caption";
    public static final String ATTRIBUTE_POST_IMAGE_KEY = "image";
    public static final String ATTRIBUTE_POST_SHARE_TO_KEY = "shareTo";
    public static final String ATTRIBUTE_POST_NB_COMMENTS_KEY = "nbComments";
    public static final String ATTRIBUTE_POST_NB_SHARES_KEY = "nbShares";
    public static final String ATTRIBUTE_POST_NB_REACTS_KEY = "nbReacts";
    public static final String ATTRIBUTE_POST_COMMENTS_KEY = "comments";
    public static final String ATTRIBUTE_POST_REACTS_KEY = "reacts";

    //CONVERSATIONS TABLE
    public static final String COLLECTION_CONVERSATIONS_KEY = "conversations";
    public static final String ATTRIBUTE_CONVERSATIONS_CONVERSATION_ID_KEY = "conversationId";
    public static final String ATTRIBUTE_CONVERSATIONS_FIRST_PARTY_KEY = "firstParty";
    public static final String ATTRIBUTE_CONVERSATIONS_SECOND_PARTY_KEY = "secondParty";
    public static final String ATTRIBUTE_CONVERSATIONS_LAST_MESSAGE_KEY = "lastMessage";
    public static final String ATTRIBUTE_CONVERSATIONS_LAST_MESSAGE_SENDER_KEY = "lastMessageSender";
    public static final String ATTRIBUTE_CONVERSATIONS_TIMESTAMP_KEY = "timestamp";
    public static final String ATTRIBUTE_CONVERSATIONS_GROUP_KEY = "group";
    public static final String ATTRIBUTE_CONVERSATIONS_SEEN_KEY = "seen";

    //CHAT_MESSAGE TABLE
    public static final String COLLECTION_CHAT_MESSAGE_KEY = "chatMessage";
    public static final String ATTRIBUTE_CHAT_MESSAGE_ID_KEY = "messageId";
    public static final String ATTRIBUTE_CHAT_MESSAGE_SENDER_ID_KEY = "senderId";
    public static final String ATTRIBUTE_CHAT_MESSAGE_SENDER_NAME_KEY = "senderName";
    public static final String ATTRIBUTE_CHAT_MESSAGE_CONVERSATION_ID_KEY = "conversationId";
    public static final String ATTRIBUTE_CHAT_MESSAGE_MESSAGE_BODY_KEY = "messageBody";
    public static final String ATTRIBUTE_CHAT_MESSAGE_IMAGE_KEY = "image";
    public static final String ATTRIBUTE_CHAT_MESSAGE_TIMESTAMP_KEY = "timestamp";




}
