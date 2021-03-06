/*
 * Copyright (c) 2015, 2016, 2017 JTS-Team authors and/or its affiliates. All rights reserved.
 *
 * This file is part of JTS-V3 Project.
 *
 * JTS-V3 Project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTS-V3 Project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JTS-V3 Project.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.jts_dev.gameserver.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.hibernate.validator.constraints.Range;
import ru.jts_dev.gameserver.ai.AiObject;
import ru.jts_dev.gameserver.inventory.CharacterInventory;
import ru.jts_dev.gameserver.parser.data.CharacterStat;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

import static ru.jts_dev.gameserver.packets.out.CharacterCreateFail.REASON_CREATION_FAILED;

/**
 * @author Camelion
 * @since 13.12.15
 */
@Entity
public class GameCharacter {
    @Id
    @GeneratedValue
    private int id;

    @Transient
    private int objectId = 10; // should be auto-generated

    @Pattern(regexp = "[A-Za-z0-9]{4,16}", message = "4-16 ENG symbols")
    @Column(unique = true)
    private String name;

    @Range(min = 0, max = 1)
    @Column
    private int sex;

    @Range(min = 0, max = 6, message = REASON_CREATION_FAILED)
    @Column
    private int hairStyle;

    @Range(min = 0, max = 3, message = REASON_CREATION_FAILED)
    @Column
    private int hairColor;

    @Range(min = 0, max = 2, message = REASON_CREATION_FAILED)
    @Column
    private int face;

    @Range(min = 0)
    @Column
    private double hp;

    @Range(min = 0)
    @Column
    private double mp;

    @Range(min = 0)
    @Column
    private int sp;

    @Range(min = 0)
    @Column
    private long exp;

    @Column
    private boolean lastUsed;

    // TODO: 26.12.15 pattern for account Name
    @Column
    private String accountName;

    // TODO: 25.12.15 computed level?
    @Column
    private int level;

    @Embedded
    private CharacterStat stat;

    @Embedded
    private CharacterInventory inventory = new CharacterInventory();

    @Transient
    private String connectionId;

    // TODO: 21.12.15 should be calculable stat
    @Transient
    private double maxHp, maxMp;

    @Transient
    private Vector3D vector3D = new Vector3D(0, 0, 0);
    @Transient
    private double angle;
    @Transient
    private boolean moving;

    @Transient
    private AiObject aiObject = new AiObject(this);

    public double getHp() {
        return hp;
    }

    public double getMaxMp() {
        return maxMp;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getObjectId() {
        return objectId;
    }

    public String getLogin() {
        return accountName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public double getMp() {
        return mp;
    }

    public int getSp() {
        return sp;
    }

    public long getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public int getHairStyle() {
        return hairStyle;
    }

    public void setHairStyle(int hairStyle) {
        this.hairStyle = hairStyle;
    }

    public int getHairColor() {
        return hairColor;
    }

    public void setHairColor(int hairColor) {
        this.hairColor = hairColor;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public double getMaxHp() {
        return maxHp;
    }

    public Vector3D getVector3D() {
        return vector3D;
    }

    public void setVector3D(Vector3D vector3D) {
        this.vector3D = vector3D;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public AiObject getAiObject() {
        return aiObject;
    }

    /**
     * this method only for hibernate mapping!!! NOT FOR USE!!!
     *
     * @return x coordinate of vector3D
     */
    @Access(AccessType.PROPERTY)
    @Column(name = "x")
    private double getX() {
        return vector3D.getX();
    }

    /**
     * this method only for hibernate mapping!!! NOT FOR USE!!!
     *
     * @param x - x character coordinate from db
     */
    private void setX(double x) {
        vector3D = new Vector3D(x, vector3D.getY(), vector3D.getZ());
    }

    /**
     * this method only for hibernate mapping!!! NOT FOR USE!!!
     *
     * @return y coordinate of vector3D
     */
    @Access(AccessType.PROPERTY)
    @Column(name = "y")
    private double getY() {
        return vector3D.getY();
    }

    /**
     * this method only for hibernate mapping!!! NOT FOR USE!!!
     *
     * @param y - y character coordinate from db
     */
    private void setY(double y) {
        vector3D = new Vector3D(vector3D.getX(), y, vector3D.getZ());
    }

    /**
     * this method only for hibernate mapping!!! NOT FOR USE!!!
     *
     * @return z coordinate of vector3D
     */
    @Access(AccessType.PROPERTY)
    @Column(name = "z")
    private double getZ() {
        return vector3D.getZ();
    }

    /**
     * this method only for hibernate mapping!!! NOT FOR USE!!!
     *
     * @param z - z character coordinate from db
     */
    private void setZ(double z) {
        vector3D = new Vector3D(vector3D.getX(), vector3D.getY(), z);
    }

    public boolean isLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(boolean lastUsed) {
        this.lastUsed = lastUsed;
    }

    public CharacterStat getStat() {
        return stat;
    }

    public void setStat(CharacterStat stat) {
        this.stat = stat;
    }

    public CharacterInventory getInventory() {
        return inventory;
    }
}
