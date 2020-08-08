/*******************************************************************************
 *  Copyright 2010, 2011 bbv Software Services AG, Ueli Kurmann
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * Contributors:
 *     bbv Software Services AG (http://www.bbv.ch), Ueli Kurmann
 *******************************************************************************/
package ch.bbv.fsm.impl.internal.driver;

/**
 * Provides information about an event: event-id and arguments.
 *
 * @author Ueli Kurmann
 * @param <E> the type of the event.
 */
final class EventHolder<E> {
	private final E eventId;
	private final Object[] eventArguments;

	/**
	 * Initializes a new instance.
	 *
	 * @param eventId        the event id.
	 * @param eventArguments the event arguments.
	 */
	private EventHolder(final E eventId, final Object[] eventArguments) {
		this.eventId = eventId;
		this.eventArguments = eventArguments;
	}

	public static <E> EventHolder<E> create(final E eventId, final Object[] eventArguments) {
		return new EventHolder<E>(eventId, eventArguments);
	}

	/**
	 * Returns the event arguments.
	 *
	 * @return the event arguments.
	 */
	public Object[] getEventArguments() {
		return this.eventArguments;
	}

	/**
	 * Returns the event id.
	 *
	 * @return the event id.
	 */
	public E getEventId() {
		return this.eventId;
	}

}
