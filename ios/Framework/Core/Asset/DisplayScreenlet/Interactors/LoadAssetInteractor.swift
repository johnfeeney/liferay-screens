/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
import UIKit


public protocol LoadAssetConnector {
	var resultAsset: Asset? { get set }
}

public class LoadAssetInteractor: ServerReadConnectorInteractor {

	public let assetEntryId: Int64?

	public let className: String?
	public let classPK: Int64?

	public var asset: Asset?

	public convenience init(screenlet: BaseScreenlet, assetEntryId: Int64) {
		self.init(screenlet: screenlet,
				assetEntryId: assetEntryId,
				className: nil,
				classPK: nil)
	}

	public convenience init(screenlet: BaseScreenlet, className: String, classPK: Int64) {
		self.init(screenlet: screenlet,
		          assetEntryId: nil,
		          className: className,
		          classPK: classPK)
	}

	private init(screenlet: BaseScreenlet, assetEntryId: Int64?, className: String?, classPK: Int64?) {
		self.assetEntryId = assetEntryId
		self.className = className
		self.classPK = classPK

		super.init(screenlet: screenlet)
	}

	override public func createConnector() -> ServerConnector? {
		if let assetEntryId = self.assetEntryId {
			return LiferayServerContext.connectorFactory.createAssetLoadByEntryIdConnector(assetEntryId)
		}
		else if let className = self.className, classPK = self.classPK {
			return LiferayServerContext.connectorFactory.createAssetLoadByClassPKConnector(className, classPK: classPK)
		}

		return nil
	}

	override public func completedConnector(c: ServerConnector) {
		asset = (c as? LoadAssetConnector)?.resultAsset
	}

	//MARK: Cache

	override public func readFromCache(c: ServerConnector, result: AnyObject? -> ()) {
		guard let cacheManager = SessionContext.currentContext?.cacheManager else {
			result(nil)
			return
		}
		guard var loadCon = c as? LoadAssetConnector else {
			result(nil)
			return
		}

		cacheManager.getAny(
				collection: "AssetsScreenlet",
				key: self.assetCacheKey) {
			loadCon.resultAsset = $0 as? Asset
			result(loadCon.resultAsset)
		}
	}

	override public func writeToCache(c: ServerConnector) {
		guard let cacheManager = SessionContext.currentContext?.cacheManager else {
			return
		}
		guard let loadCon = c as? LoadAssetConnector else {
			return
		}
		guard let asset = loadCon.resultAsset else {
			return
		}

		cacheManager.setClean(
			collection: "AssetsScreenlet",
			key: self.assetCacheKey,
			value: asset,
			attributes: [
				"entryId": NSNumber(longLong: assetEntryId ?? 0),
				"className": className ?? "",
				"classPK": NSNumber(longLong: classPK ?? 0),
			])
	}

	private var assetCacheKey: String {
		if let assetEntryId = self.assetEntryId {
			return "load-asset-entryId-\(assetEntryId)"
		}
		else if let className = self.className, classPK = self.classPK {
			return "load-asset-cn-\(className)-cpk-\(classPK)"
		}

		fatalError("Need either assetEntryId or className+classPK")
	}

}