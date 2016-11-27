package ir.realenglish.app.app;

public class EndPoints {
    public static final String BASE_URL = "http://realenglish.ir"; //http://10.0.2.2:8585
    public static final String BASE_API = BASE_URL + "/api/v1";

    // POST URLS
    public static final String POST_BASE = BASE_API + "/posts?page=_R_";
    public static final String POST_SEND = BASE_API + "/posts";
    public static final String POST_UPDATE = BASE_API + "/posts/_R_";
    public static final String POST_COMMENTS = BASE_API + "/posts/_R_/comments";
    public static final String POST_SHOW = BASE_API + "/posts/_R_";
    public static final String POST_SEARCH = BASE_API + "/posts/search?q=_R_";


    // COMMENT URLS
    public static final String COMMENT_SEND = BASE_API + "/comments";
    public static final String COMMENT_UPDATE_AND_DELETE = BASE_API + "/comments/_R_";

    // REPORT URLS
    public static final String REPORT_SEND = BASE_API + "/reports";

    // SCORE
    public static final String SCORE_BASE = BASE_API + "/scores";

    // FAVORITE URLS
    public static final String FAVORITE_SEND = BASE_API + "/favorites";

    // USER URLS
    public static final String BASE_USERS = BASE_API + "/users";
    public static final String USER_PROFILE_BASIC_INFO = BASE_USERS + "/_R_/profile?api_token=";
    public static final String USER_POSTS = BASE_API + "/users/_R1_/posts?page=_R2_";
    public static final String USER_COMMENTS = BASE_API + "/users/_R1_/comments";
    public static final String USER_POST_FAVORITES = BASE_API + "/users/_R1_/favorites/posts?page=_R2_&ids=_R3_";
    public static final String USER_FAVORITE_LESSONS = BASE_API + "/users/_R1_/favorites/lessons?page=_R2_&ids=";
    public static final String USER_TOP = BASE_USERS + "/top";
    public static final String USER_IMAGES = BASE_API + "/users/_R_/images";
    public static final String USER_SCORE = BASE_USERS + "/_R_/score";


    // LESSON
    public static final String LESSONS_BASE = BASE_API + "/lessons";
    public static final String LESSONS_GET = LESSONS_BASE + "?page=_R_";
    public static final String LESSON_COMMENTS = BASE_API + "/lessons/_R1_/comments";
    public static final String LESSON_SHOW = BASE_API + "/lessons/_R_";
    public static final String LESSONS_SEARCH = LESSONS_BASE + "/search?q=_R_";
    public static final String LESSON_QUIZ = LESSONS_BASE + "/_R_/test";
    public static final String LESSON_EDIT_GET = LESSONS_BASE + "/_R_/edit?api_token=";
    public static final String LESSON_EDIT_PATCH = LESSONS_BASE + "/_R_";



    public static final String TAG_LIST = BASE_API + "/tag";

    // MEDIA URLS
    public static final String IMAGE_URL = BASE_URL + "/file/image";
    public static final String UPLOAD_IMAGE = BASE_API + "/upload/image";
    public static final String AUDIO_URL = BASE_URL + "/file/audio";
    public static final String LESSON_FILE_DIRECTORY = BASE_URL + "/file/lesson";
    public static final String POST_FILE_DIRECTORY = BASE_URL + "/file/post";
}
