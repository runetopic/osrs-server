package com.osrs.game.actor.render

import com.osrs.game.actor.Actor
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

abstract class RenderBlock<T : Actor, R : RenderType>(open val builder: RenderBlockBuilder<T, R>)
