/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.program.database.mem;

import java.util.Optional;

import ghidra.program.model.address.Address;
import ghidra.program.model.address.AddressRange;
import ghidra.program.model.mem.MemoryBlock;
import ghidra.program.model.mem.MemoryBlockSourceInfo;

/** 
 * Class for describing the source of bytes for a memory block.
 */
class MemoryBlockSourceInfoDB implements MemoryBlockSourceInfo {

	private final MemoryBlock block;
	private final SubMemoryBlock subBlock;

	MemoryBlockSourceInfoDB(MemoryBlock block, SubMemoryBlock subBlock) {
		this.block = block;
		this.subBlock = subBlock;
	}

	/**
	 * @return
	 */
	@Override
	public long getLength() {
		return subBlock.length;
	}

	/**
	 * @return
	 */
	@Override
	public Address getMinAddress() {
		return block.getStart().add(subBlock.startingOffset);
	}

	/**
	 * @return
	 */
	@Override
	public Address getMaxAddress() {
		return block.getStart().add(subBlock.startingOffset + subBlock.length - 1);
	}

	/**
	 * @return
	 */
	@Override
	public String getDescription() {
		return subBlock.getDescription();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": StartAddress = " + getMinAddress() + ", length = " +
			getLength();

	}

	/**
	 * @return
	 */
	@Override
	public Optional<FileBytes> getFileBytes() {
		if (subBlock instanceof FileBytesSubMemoryBlock) {
			return Optional.of(((FileBytesSubMemoryBlock) subBlock).getFileBytes());
		}
		return Optional.empty();
	}

	/**
	 * @return
	 */
	@Override
	public long getFileBytesOffset() {
		if (subBlock instanceof FileBytesSubMemoryBlock) {
			return ((FileBytesSubMemoryBlock) subBlock).getFileBytesOffset();
		}
		return -1;
	}

	/**
	 * @param address
	 * @return
	 */
	@Override
	public long getFileBytesOffset(Address address) {
		if (subBlock instanceof FileBytesSubMemoryBlock && contains(address)) {
			long blockOffset = address.subtract(getMinAddress());
			long subBlockOffset = blockOffset - subBlock.startingOffset;
			return ((FileBytesSubMemoryBlock) subBlock).getFileBytesOffset() + subBlockOffset;
		}
		return -1;
	}

	/**
	 * @return
	 */
	@Override
	public Optional<AddressRange> getMappedRange() {
		if (subBlock instanceof BitMappedSubMemoryBlock) {
			BitMappedSubMemoryBlock bitMapped = (BitMappedSubMemoryBlock) subBlock;
			return Optional.of(bitMapped.getMappedRange());
		}
		if (subBlock instanceof ByteMappedSubMemoryBlock) {
			ByteMappedSubMemoryBlock byteMapped = (ByteMappedSubMemoryBlock) subBlock;
			return Optional.of(byteMapped.getMappedRange());
		}
		return Optional.empty();
	}

	/**
	 * @return
	 */
	@Override
	public MemoryBlock getMemoryBlock() {
		return block;
	}

	/**
	 * @param address
	 * @return
	 */
	@Override
	public boolean contains(Address address) {
		return address.compareTo(getMinAddress()) >= 0 && address.compareTo(getMaxAddress()) <= 0;
	}
}
