package me.oogh.similar.data.entry;

import java.util.List;

/**
 * Created by oogh on 18-3-20.
 */

public class Event {
    public enum Tag {
        MURMUR_ADD_COMPLETED, // 添加 Murmur
        MURMUR_EDIT_COMPLETED, // 编辑 Murmur
        MURMUR_PARAM, // 传递 Murmur
        MURMUR_REMOVE, // 移除 Murmur
        MURMUR_SHOW_LIST, // 显示列表
        MURMUR_SHOW_EMPTY, // 显示空白
        MURMUR_CACHE_LIST, // 显示缓存的列表
        WHISPER_TOPIC_SELECTED, // 心愿主题被选中
        WHISPER_ADD_COMPLETED // 添加 whisper
    }

    public static class MurmurUpdateEvent {
    }

    public static class MurmurListEvent {
        public List<Murmur> murmurs;
        public Tag tag;

        public MurmurListEvent(List<Murmur> murmurs, Tag tag) {
            this.murmurs = murmurs;
            this.tag = tag;
        }
    }

    public static class MurmurEvent {
        public Murmur murmur;
        public Tag tag;

        public MurmurEvent(Murmur murmur, Tag tag) {
            this.murmur = murmur;
            this.tag = tag;
        }
    }

    public static class WhisperTopicSelectedEvent {
        public Topic topic;
        public Tag tag;

        public WhisperTopicSelectedEvent(Topic topic, Tag tag) {
            this.topic = topic;
            this.tag = tag;
        }
    }

    public static class WhisperEvent {
        public Whisper whisper;
        public Tag tag;

        public WhisperEvent(Whisper whisper, Tag tag) {
            this.whisper = whisper;
            this.tag = tag;
        }
    }
}
