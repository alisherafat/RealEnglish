package ir.realenglish.app.event;

import ir.realenglish.app.model.Lesson;
import ir.realenglish.app.model.Post;

/**
 * Created by ALI on 8/27/2016.
 */
public class MyEvent {
    public static class DownloadComplete {
    }

    public static class LessonReceived {
        public Lesson lesson;

        public LessonReceived(Lesson lesson) {
            this.lesson = lesson;
        }

    }

    public static class RefreshNavigation {
    }

    public static class SearchSubmit {
        private String query;

        public SearchSubmit(String query) {
            this.query = query;
        }

        public String getQuery() {
            return query;
        }
    }

    public static class PostDataReceived {
        private Post post;

        public PostDataReceived(Post post) {
            this.post = post;
        }

        public Post getPost() {
            return this.post;
        }
    }

    public static class PostCommentsReceive {
    }
}
