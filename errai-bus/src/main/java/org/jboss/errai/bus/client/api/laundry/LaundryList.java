/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.bus.client.api.laundry;


/**
 * A collection of tasks that should be performed when a particular session has ended.
 * <p>
 * Example code for adding a task to a session's laundry list:
 * <pre>
 * Message message = ...;
 * Object sessionResource = message.getResource(Object.class, "Session");
 * final LaundryReclaim reclaim =
 *         LaundryListProviderFactory.get()
 *             .getLaundryList(sessionResource)
 *             .add(new Laundry() {
 *               {@code @Override}
 *               public void clean() {
 *                 task.cancel(true);
 *               }
 *         });
 * </pre>
 * 
 * @author Mike Brock
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface LaundryList {
  
  /**
   * Adds a task.
   * 
   * @param laundry  The task to run. Cannot be null.
   * @return A handle on the task that can be used to run the task early (before the session has ended).
   */
  public LaundryReclaim add(Laundry laundry);

  /**
   * Removes a task.
   * 
   * @param laundry  The task to remove. 
   * @return true if removed, otherwise false.
   */
  public boolean remove(Laundry laundry);
}
