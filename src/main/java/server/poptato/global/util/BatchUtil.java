package server.poptato.global.util;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BatchUtil {

    /**
     * 리스트를 지정된 크기(batch size)로 나눕니다.
     *
     * @param list 원본 리스트
     * @param size 나눌 각 배치의 최대 크기
     * @param <T>  리스트 요소의 타입
     * @return 나뉜 리스트의 리스트 (배치 단위)
     */
    public static <T> List<List<T>> splitIntoBatches(List<T> list, int size) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        int numBatches = (list.size() + size - 1) / size;

        return IntStream.range(0, numBatches)
                .mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, list.size())))
                .collect(Collectors.toList());
    }
}
