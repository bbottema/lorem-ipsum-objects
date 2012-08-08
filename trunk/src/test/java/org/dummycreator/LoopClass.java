/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.opensource.org/licenses/cddl1.php
 * or http://www.opensource.org/licenses/cddl1.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.opensource.org/licenses/cddl1.php.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is dummyCreator. The Initial Developer of the Original
 * Software is Alexander Muthmann <amuthmann@dev-eth0.de>.
 */
package org.dummycreator;

public class LoopClass {

    protected LoopClass loopObject;
    protected long id;

    /**
     * Get the value of id
     * 
     * @return the value of id
     */
    public long getId() {
	return id;
    }

    /**
     * Set the value of id
     * 
     * @param id new value of id
     */
    public void setId(long id) {
	this.id = id;
    }

    /**
     * Get the value of loopObject
     * 
     * @return the value of loopObject
     */
    public LoopClass getLoopObject() {
	return loopObject;
    }

    /**
     * Set the value of loopObject
     * 
     * @param loopObject new value of loopObject
     */
    public void setLoopObject(LoopClass loopObject) {
	this.loopObject = loopObject;
    }
}
