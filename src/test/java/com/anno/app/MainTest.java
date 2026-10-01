package com.anno.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 基线工程的入口测试。
 *
 * <p>作用有两个：一是保证 {@code mvn test} 在初始状态下即为绿色，
 * 后续任务新增测试时能区分"本来就坏"和"被改坏"；
 * 二是给出捕获标准输出的写法，便于任务实现后对 CLI 行为做断言。</p>
 */
class MainTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(errContent, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("无参数时打印用法并以 0 退出")
    void noArgsPrintsUsage() {
        assertEquals(0, Main.run(new String[]{}));
        assertTrue(outContent.toString(StandardCharsets.UTF_8).contains("用法"));
    }

    @Test
    @DisplayName("ping 输出 pong（最小验证命令）")
    void pingReturnsPong() {
        assertEquals(0, Main.run(new String[]{"ping"}));
        assertEquals("pong", outContent.toString(StandardCharsets.UTF_8).trim());
    }

    @Test
    @DisplayName("version 输出带版本号的标识")
    void versionPrintsVersion() {
        assertEquals(0, Main.run(new String[]{"version"}));
        assertTrue(outContent.toString(StandardCharsets.UTF_8).contains(Main.VERSION));
    }

    @Test
    @DisplayName("未知命令走错误输出并以非 0 退出")
    void unknownCommandFails() {
        assertEquals(2, Main.run(new String[]{"definitely-not-a-command"}));
        assertTrue(errContent.toString(StandardCharsets.UTF_8).contains("未知命令"));
    }
}
