package io.github.go4it.ai.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class LogAdvisor implements CallAdvisor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        LOGGER.info("--- Outgoing prompt ({} messages) ---", request.prompt().getInstructions().size());
        request.prompt().getInstructions().forEach(instruction ->
                LOGGER.info("Instruction: role={} content={} metadata={}",
                        instruction.getMessageType(),
                        instruction.getText(),
                        instruction.getMetadata()
                )
        );

        final ChatClientResponse response = chain.nextCall(request);

        response.chatResponse().getResults().forEach(generation ->
                LOGGER.info("--- Model reply: finish={} toolCalls={} text={}",
                        generation.getMetadata().getFinishReason(),
                        generation.getOutput().getToolCalls(),
                        generation.getOutput().getText()
                )
        );

        return response;
    }

    @Override
    public String getName() {
        return "LogAdvisor";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 400;
    }
}
