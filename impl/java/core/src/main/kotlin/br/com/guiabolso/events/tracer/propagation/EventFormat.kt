package br.com.guiabolso.events.tracer.propagation

import io.opentracing.propagation.Format

object EventFormat : Format<EventTextMapAdapter>
