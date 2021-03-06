package com.example.tptdl.gamelogic.tokens

import com.example.tptdl.R
import com.example.tptdl.gamelogic.gameboard.Cell
import com.example.tptdl.gamelogic.gameboard.GameBoard
import com.example.tptdl.gamelogic.gameboard.RuleSet

// For now this class will implement Token
class Bomb(val ruleSet: RuleSet) : Explosive {   // explosionRadius is a 3x3 by default, it should always be an odd number
    private var canBeExploded = true
    override val pointValue = 0
    private var explosionRadius = ruleSet.obtainExplosionRadius()

    override fun toString() : String {
        return "Bomb"
    }

    // If any exploded cells are out of bounds, obtainCell returns Void(), and when Void() is .pop()'ed, no score is added
    override fun explode(cellCoords: Pair<Int, Int>, gameBoard: GameBoard) {
        this.cannotBeExploded() // need to set this to false first in case of a bomb exploding another bomb, in which case they will recursively blow each other up until stackOverflow
        val adjacentCellsInRadius : MutableList<Cell> = mutableListOf()
        val (x, y) = cellCoords    // coordinates of the center of the explosion
        val bordersFromCenter = explosionRadius/2   // Example: if 3x3, borders are 1 cell away from center, if 5x5 2, 7x7 3.
        for (i in 0 until explosionRadius) { // width of explosion
            for (j in 0 until explosionRadius) { // height of explosion
                val xCoord = x-bordersFromCenter+i
                val yCoord = y-bordersFromCenter+j
                val explodedCellCoords = Pair(xCoord, yCoord)
                val explodedCell = gameBoard.obtainCell(explodedCellCoords)
                if (explodedCell.isExplosive())
                    explodedCell.explode(explodedCellCoords, gameBoard)
                adjacentCellsInRadius.add(explodedCell)
            }
        }
        adjacentCellsInRadius.forEach { it.pop(gameBoard.getScore()) }
    }

    override fun isExplosive(): Boolean {
        return canBeExploded
    }

    private fun cannotBeExploded() {
        canBeExploded = false
    }

    override fun getPath() : Int {
        return ruleSet.getBombPath()
    }

}
