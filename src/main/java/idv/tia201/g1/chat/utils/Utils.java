package idv.tia201.g1.chat.utils;

import idv.tia201.g1.chat.dto.MessageDTO;

public class Utils {
    public static boolean isImageEmpty(MessageDTO.ImageDTO img) {
        return img == null || img.getSrc() == null || img.getSrc().trim().isEmpty();
    }
}
