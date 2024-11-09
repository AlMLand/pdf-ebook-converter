package com.almland.pdfebookconverter.infrastructure.aopadvice

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
internal class AggregateQueryPortAspect {

    private val logger = LoggerFactory.getLogger("performance.logger")

    @Around("this(com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort)")
    fun aroundAggregateQueryPortAdvice(joinPoint: ProceedingJoinPoint): Any? =
        System.currentTimeMillis().let { start ->
            try {
                joinPoint.proceed()
            } finally {
                logger.info(
                    "Duration of {} execution was {} millis",
                    joinPoint.signature.toShortString(),
                    System.currentTimeMillis() - start
                )
            }
        }
}
