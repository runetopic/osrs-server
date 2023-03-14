package com.osrs.game.actor.render

import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

abstract class RenderBlock<R : RenderType>(open val builder: RenderBlockBuilder<R>)
